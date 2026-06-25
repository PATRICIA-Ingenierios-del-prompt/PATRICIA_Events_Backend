package ingprompt.patricia.events.infrastructure.persistence;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.enums.ReportType;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.domain.model.Location;
import ingprompt.patricia.events.domain.model.Report;
import ingprompt.patricia.events.infrastructure.persistence.postgre.EventRepository;
import ingprompt.patricia.events.infrastructure.persistence.postgre.ParcheMembershipRepository;
import ingprompt.patricia.events.infrastructure.persistence.postgre.ReportRepository;
import ingprompt.patricia.events.infrastructure.persistence.repository.EventRepositoryAdapter;
import ingprompt.patricia.events.infrastructure.persistence.repository.ParcheMembershipRepositoryAdapter;
import ingprompt.patricia.events.infrastructure.persistence.repository.ReportRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the Postgres persistence adapters (events, parche
 * memberships, reports) against a real PostgreSQL via Testcontainers.
 * Requires Docker — executed by Failsafe in CI.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class EventPersistenceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ParcheMembershipRepository membershipRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private TestEntityManager entityManager;

    private EventRepositoryAdapter events;
    private ParcheMembershipRepositoryAdapter memberships;
    private ReportRepositoryAdapter reports;

    private final Pageable firstPage = PageRequest.of(0, 20);
    private final Location destination = new Location(4.65, -74.05, "Calle 1", "place-1");

    @BeforeEach
    void setUp() {
        events = new EventRepositoryAdapter(eventRepository);
        memberships = new ParcheMembershipRepositoryAdapter(membershipRepository);
        reports = new ReportRepositoryAdapter(reportRepository);
    }

    private Event event(String name, Category category, LocalDate date, int maxCapacity, int extraParticipants) {
        Event event = new Event(UUID.randomUUID(), name, "desc", category, maxCapacity, UUID.randomUUID(),
                date, LocalTime.of(10, 0), LocalTime.of(12, 0));
        event.setDestination(destination);
        for (int i = 0; i < extraParticipants; i++) {
            event.addParticipant(UUID.randomUUID());
        }
        return event;
    }

    // ---- EventRepositoryAdapter ----

    @Test
    void saveAndFind_roundTrips() {
        Event event = event("Hike", Category.SPORT, LocalDate.now().plusDays(1), 10, 1);
        events.save(event);

        Event loaded = events.findById(event.getEventId()).orElseThrow();
        assertThat(loaded.getName()).isEqualTo("Hike");
        assertThat(loaded.getUsersInscribed()).hasSize(2); // owner + 1
        assertThat(loaded.getDestination().getAddress()).isEqualTo("Calle 1");
    }

    @Test
    void delete_removesRow() {
        Event event = event("Gone", Category.ART, LocalDate.now().plusDays(1), 10, 0);
        events.save(event);

        events.delete(event);

        assertThat(events.findById(event.getEventId())).isEmpty();
    }

    @Test
    void deleteAllByIds_bulkDeletes() {
        Event a = event("A", Category.MUSIC, LocalDate.now().plusDays(1), 10, 0);
        Event b = event("B", Category.MUSIC, LocalDate.now().plusDays(1), 10, 0);
        events.save(a);
        events.save(b);
        // Flush the inserts and detach them so the bulk DELETE hits the DB and
        // the later reads aren't served stale from the persistence context.
        entityManager.flush();
        entityManager.clear();

        events.deleteAllByIds(List.of(a.getEventId(), b.getEventId()));
        entityManager.clear();

        assertThat(events.findById(a.getEventId())).isEmpty();
        assertThat(events.findById(b.getEventId())).isEmpty();
    }

    @Test
    void findByCategory_filters() {
        events.save(event("Sport", Category.SPORT, LocalDate.now().plusDays(1), 10, 0));
        events.save(event("Art", Category.ART, LocalDate.now().plusDays(1), 10, 0));

        Page<Event> result = events.findByCategory(Category.SPORT, firstPage);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Sport");
    }

    @Test
    void findByNameContaining_isCaseInsensitive() {
        events.save(event("Mountain Hike", Category.SPORT, LocalDate.now().plusDays(1), 10, 0));
        events.save(event("Beach Day", Category.SPORT, LocalDate.now().plusDays(1), 10, 0));

        Page<Event> result = events.findByNameContaining("hike", firstPage);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Mountain Hike");
    }

    @Test
    void findByEventDate_filters() {
        LocalDate target = LocalDate.now().plusDays(3);
        events.save(event("Target", Category.SPORT, target, 10, 0));
        events.save(event("Other", Category.SPORT, LocalDate.now().plusDays(5), 10, 0));

        Page<Event> result = events.findByEventDate(target, firstPage);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Target");
    }

    @Test
    void findWithOpenSlots_excludesFullEvents() {
        events.save(event("Full", Category.SPORT, LocalDate.now().plusDays(1), 2, 1)); // owner + 1 = 2 = full
        events.save(event("Open", Category.SPORT, LocalDate.now().plusDays(1), 5, 0)); // owner only

        Page<Event> result = events.findWithOpenSlots(firstPage);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Open");
    }

    @Test
    void findStartableAndFinishableCandidates_work() {
        events.save(event("Soon", Category.SPORT, LocalDate.now(), 10, 0));

        assertThat(events.findStartableCandidates(LocalDate.now())).hasSize(1);
        assertThat(events.findFinishableCandidates(LocalDate.now())).hasSize(1);
    }

    // ---- ParcheMembershipRepositoryAdapter ----

    @Test
    void membership_saveExistsDelete() {
        UUID parcheId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        memberships.save(parcheId, userId);
        assertThat(memberships.exists(parcheId, userId)).isTrue();

        memberships.delete(parcheId, userId);
        assertThat(memberships.exists(parcheId, userId)).isFalse();
    }

    @Test
    void membership_deleteAllByParcheId() {
        UUID parcheId = UUID.randomUUID();
        memberships.save(parcheId, UUID.randomUUID());
        memberships.save(parcheId, UUID.randomUUID());

        memberships.deleteAllByParcheId(parcheId);

        assertThat(membershipRepository.count()).isZero();
    }

    // ---- ReportRepositoryAdapter ----

    @Test
    void report_savePersistsRow() {
        Report report = Report.create(UUID.randomUUID(), UUID.randomUUID(), ReportType.ACCIDENT, "fell");

        reports.save(report);

        assertThat(reportRepository.findById(report.getReportId())).isPresent();
    }
}
