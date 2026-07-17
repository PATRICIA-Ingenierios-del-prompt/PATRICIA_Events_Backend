package ingprompt.patricia.events.infrastructure.persistence.repository;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.infrastructure.persistence.entity.EventEntity;
import ingprompt.patricia.events.infrastructure.persistence.postgre.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRepositoryAdapterTest {

    @Mock
    private EventRepository repository;

    private EventRepositoryAdapter adapter;

    private final UUID eventId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new EventRepositoryAdapter(repository);
    }

    private EventEntity entity() {
        EventEntity e = new EventEntity();
        e.setEventId(eventId);
        e.setName("Hike");
        e.setDescription("desc");
        e.setOwnerId(ownerId);
        e.setCategory(Category.SPORT);
        e.setMaxCapacity(10);
        e.setEventDate(LocalDate.now().plusDays(1));
        e.setStartTime(LocalTime.of(10, 0));
        e.setEndTime(LocalTime.of(12, 0));
        e.setUsersInscribed(Set.of(ownerId));
        return e;
    }

    private Event domain() {
        return new Event(eventId, "Hike", "desc", Category.SPORT, 10, ownerId,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
    }

    @Test
    void save_mapsToEntityAndPersists() {
        adapter.save(domain());

        verify(repository).save(any(EventEntity.class));
    }

    @Test
    void delete_removesById() {
        adapter.delete(domain());

        verify(repository).deleteById(eventId);
    }

    @Test
    void findById_whenPresent_mapsToDomain() {
        when(repository.findById(eventId)).thenReturn(Optional.of(entity()));

        Optional<Event> result = adapter.findById(eventId);

        assertThat(result).isPresent();
        assertThat(result.get().getEventId()).isEqualTo(eventId);
        assertThat(result.get().getName()).isEqualTo("Hike");
    }

    @Test
    void deleteAllByIds_whenEmpty_doesNothing() {
        adapter.deleteAllByIds(List.of());

        verify(repository, never()).deleteAllByIdInBatch(any());
    }

    @Test
    void deleteAllByIds_whenNull_doesNothing() {
        adapter.deleteAllByIds(null);

        verify(repository, never()).deleteAllByIdInBatch(any());
    }

    @Test
    void deleteAllByIds_whenNonEmpty_deletesInBatch() {
        List<UUID> ids = List.of(eventId);

        adapter.deleteAllByIds(ids);

        verify(repository).deleteAllByIdInBatch(ids);
    }

    @Test
    void findStartableCandidates_mapsResults() {
        LocalDate date = LocalDate.now();
        when(repository.findByStartedFalseAndEventDateLessThanEqual(date)).thenReturn(List.of(entity()));

        assertThat(adapter.findStartableCandidates(date)).hasSize(1);
    }

    @Test
    void findFinishableCandidates_mapsResults() {
        LocalDate date = LocalDate.now();
        when(repository.findByFinishedFalseAndEventDateLessThanEqual(date)).thenReturn(List.of(entity()));

        assertThat(adapter.findFinishableCandidates(date)).hasSize(1);
    }

    @Test
    void findByCategory_mapsPage() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findVisibleOpenByCategory(Category.SPORT, pageable)).thenReturn(new PageImpl<>(List.of(entity())));

        Page<Event> page = adapter.findByCategory(Category.SPORT, pageable);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getEventId()).isEqualTo(eventId);
    }

    @Test
    void findByNameContaining_mapsPage() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findVisibleOpenByName("hike", pageable)).thenReturn(new PageImpl<>(List.of(entity())));

        assertThat(adapter.findByNameContaining("hike", pageable).getContent()).hasSize(1);
    }

    @Test
    void findByEventDate_mapsPage() {
        Pageable pageable = PageRequest.of(0, 20);
        LocalDate date = LocalDate.now().plusDays(1);
        when(repository.findVisibleOpenByDate(date, pageable)).thenReturn(new PageImpl<>(List.of(entity())));

        assertThat(adapter.findByEventDate(date, pageable).getContent()).hasSize(1);
    }

    @Test
    void findPublicOpenEvents_mapsPage() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findPublicOpenEvents(pageable)).thenReturn(new PageImpl<>(List.of(entity())));

        assertThat(adapter.findPublicOpenEvents(pageable).getContent()).hasSize(1);
    }

    @Test
    void findOpenEventsForParches_mapsPage() {
        Pageable pageable = PageRequest.of(0, 20);
        List<UUID> parcheIds = List.of(UUID.randomUUID());
        when(repository.findOpenEventsForParches(parcheIds, pageable)).thenReturn(new PageImpl<>(List.of(entity())));

        assertThat(adapter.findOpenEventsForParches(parcheIds, pageable).getContent()).hasSize(1);
    }

    @Test
    void findJoinedByUser_mapsPage() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findJoinedByUser(ownerId, pageable)).thenReturn(new PageImpl<>(List.of(entity())));

        Page<Event> page = adapter.findJoinedByUser(ownerId, pageable);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getOwnerId()).isEqualTo(ownerId);
    }
}
