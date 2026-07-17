package ingprompt.patricia.events.domain.model;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.exception.CannotRemoveOwnerException;
import ingprompt.patricia.events.domain.exception.EventIsFullException;
import ingprompt.patricia.events.domain.exception.InvalidEventLocationException;
import ingprompt.patricia.events.domain.exception.InvalidEventScheduleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventTest {

    private UUID ownerId;
    private UUID eventId;
    private Location validDestination;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        validDestination = new Location(4.65, -74.05, "Calle 1", "place-1");
    }

    private Event event(LocalDate date, LocalTime start, LocalTime end) {
        return new Event(eventId, "Hike", "desc", Category.SPORT, 10, ownerId, date, start, end);
    }

    private LocalDateTime creationBefore(LocalDate date, LocalTime start) {
        // Creation time comfortably before the event start, satisfying the lead time.
        return LocalDateTime.of(date, start).minusHours(2);
    }

    @Test
    void constructor_addsOwnerAsParticipant() {
        Event event = event(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThat(event.getUsersInscribed()).containsExactly(ownerId);
        assertThat(event.isOwnedBy(ownerId)).isTrue();
        assertThat(event.hasParticipant(ownerId)).isTrue();
    }

    @Test
    void validateSchedule_valid_passes() {
        LocalDate date = LocalDate.now().plusDays(1);
        Event event = event(date, LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThatCode(() -> event.validateSchedule(creationBefore(date, LocalTime.of(10, 0))))
                .doesNotThrowAnyException();
    }

    @Test
    void validateSchedule_nullFields_throws() {
        Event event = event(null, null, null);

        assertThatThrownBy(() -> event.validateSchedule(LocalDateTime.now()))
                .isInstanceOf(InvalidEventScheduleException.class)
                .hasMessageContaining("required");
    }

    @Test
    void validateSchedule_pastDate_throws() {
        LocalDate past = LocalDate.now().minusDays(1);
        Event event = event(past, LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThatThrownBy(() -> event.validateSchedule(LocalDateTime.now()))
                .isInstanceOf(InvalidEventScheduleException.class)
                .hasMessageContaining("past");
    }

    @Test
    void validateSchedule_leadTimeTooShort_throws() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime start = LocalTime.of(10, 0);
        Event event = event(date, start, LocalTime.of(12, 0));

        // Creation only 5 minutes before start (< 30 min MIN_LEAD_TIME).
        LocalDateTime createdAt = LocalDateTime.of(date, start).minusMinutes(5);

        assertThatThrownBy(() -> event.validateSchedule(createdAt))
                .isInstanceOf(InvalidEventScheduleException.class)
                .hasMessageContaining("minutes after creation");
    }

    @Test
    void validateSchedule_startEqualsEnd_throws() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime same = LocalTime.of(10, 0);
        Event event = event(date, same, same);

        assertThatThrownBy(() -> event.validateSchedule(creationBefore(date, same)))
                .isInstanceOf(InvalidEventScheduleException.class)
                .hasMessageContaining("must differ");
    }

    @Test
    void validateLocations_missingDestination_throws() {
        Event event = event(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThatThrownBy(event::validateLocations)
                .isInstanceOf(InvalidEventLocationException.class)
                .hasMessageContaining("destination");
    }

    @Test
    void validateLocations_incompleteMeetingPoint_throws() {
        Event event = event(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
        event.setDestination(validDestination);
        event.setMeetingPoint(new Location(null, null, "somewhere", null));

        assertThatThrownBy(event::validateLocations)
                .isInstanceOf(InvalidEventLocationException.class)
                .hasMessageContaining("Meeting point");
    }

    @Test
    void validateLocations_validDestinationOnly_passes() {
        Event event = event(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
        event.setDestination(validDestination);

        assertThatCode(event::validateLocations).doesNotThrowAnyException();
    }

    @Test
    void endsAt_whenEndAfterStart_sameDay() {
        LocalDate date = LocalDate.now().plusDays(1);
        Event event = event(date, LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThat(event.endsAt()).isEqualTo(LocalDateTime.of(date, LocalTime.of(12, 0)));
    }

    @Test
    void endsAt_whenEndBeforeStart_rollsToNextDay() {
        LocalDate date = LocalDate.now().plusDays(1);
        Event event = event(date, LocalTime.of(23, 0), LocalTime.of(1, 0));

        assertThat(event.endsAt()).isEqualTo(LocalDateTime.of(date.plusDays(1), LocalTime.of(1, 0)));
    }

    @Test
    void markStartedAndFinished_toggleFlags() {
        Event event = event(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        event.markStarted();
        event.markFinished();

        assertThat(event.isStarted()).isTrue();
        assertThat(event.isFinished()).isTrue();
    }

    @Test
    void addParticipant_whenAlreadyPresent_isNoOp() {
        Event event = event(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        event.addParticipant(ownerId); // already inscribed as owner

        assertThat(event.getUsersInscribed()).containsExactly(ownerId);
    }

    @Test
    void addParticipant_whenFull_throws() {
        Event event = new Event(eventId, "Hike", "desc", Category.SPORT, 1, ownerId,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThatThrownBy(() -> event.addParticipant(UUID.randomUUID()))
                .isInstanceOf(EventIsFullException.class);
    }

    @Test
    void addParticipant_whenRoomAvailable_adds() {
        Event event = event(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
        UUID joiner = UUID.randomUUID();

        event.addParticipant(joiner);

        assertThat(event.hasParticipant(joiner)).isTrue();
    }

    @Test
    void removeParticipant_owner_throws() {
        Event event = event(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThatThrownBy(() -> event.removeParticipant(ownerId))
                .isInstanceOf(CannotRemoveOwnerException.class);
    }

    @Test
    void removeParticipant_member_removes() {
        Event event = event(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
        UUID member = UUID.randomUUID();
        event.addParticipant(member);

        event.removeParticipant(member);

        assertThat(event.hasParticipant(member)).isFalse();
    }

    @Test
    void isLinkedToParche_reflectsParcheId() {
        UUID parcheId = UUID.randomUUID();
        Event standalone = event(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
        Event linked = new Event(eventId, "Hike", "desc", Category.SPORT, 10, parcheId, ownerId,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThat(standalone.isLinkedToParche()).isFalse();
        assertThat(linked.isLinkedToParche()).isTrue();
    }
}
