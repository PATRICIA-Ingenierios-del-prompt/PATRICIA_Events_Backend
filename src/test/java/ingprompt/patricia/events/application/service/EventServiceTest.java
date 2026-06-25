package ingprompt.patricia.events.application.service;

import ingprompt.patricia.events.application.port.out.EventPublisherOut;
import ingprompt.patricia.events.application.port.out.EventRepositoryOutPort;
import ingprompt.patricia.events.application.port.out.ParcheMembershipRepositoryOutPort;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.exception.CannotRemoveOwnerException;
import ingprompt.patricia.events.domain.exception.EventIsFullException;
import ingprompt.patricia.events.domain.exception.EventNotFoundException;
import ingprompt.patricia.events.domain.exception.InvalidEventLocationException;
import ingprompt.patricia.events.domain.exception.InvalidEventScheduleException;
import ingprompt.patricia.events.domain.exception.NotEventOwnerException;
import ingprompt.patricia.events.domain.exception.NotParcheMemberException;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.domain.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepositoryOutPort repository;
    @Mock
    private EventPublisherOut publisher;
    @Mock
    private ParcheMembershipRepositoryOutPort membershipRepository;

    @InjectMocks
    private EventService service;

    private UUID ownerId;
    private UUID eventId;
    private Location destination;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        destination = new Location(4.65, -74.05, "Calle 1", "place-1");
    }

    private LocalDate tomorrow() {
        return LocalDate.now().plusDays(1);
    }

    private Event existingEvent(int maxCapacity) {
        Event event = new Event(eventId, "Hike", "desc", Category.SPORT, maxCapacity, ownerId,
                tomorrow(), LocalTime.of(10, 0), LocalTime.of(12, 0));
        event.setDestination(destination);
        return event;
    }

    // ---- ManageEventCase ----

    @Test
    void createEvent_valid_savesAndReturns() {
        Event result = service.createEvent("Hike", "desc", Category.SPORT, 10, ownerId,
                tomorrow(), LocalTime.of(10, 0), LocalTime.of(12, 0), null, destination, "pic");

        assertThat(result.getOwnerId()).isEqualTo(ownerId);
        assertThat(result.getUsersInscribed()).contains(ownerId);
        verify(repository).save(any(Event.class));
    }

    @Test
    void createEvent_withPastDate_throwsScheduleException() {
        assertThatThrownBy(() -> service.createEvent("Hike", "desc", Category.SPORT, 10, ownerId,
                LocalDate.now().minusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0), null, destination, null))
                .isInstanceOf(InvalidEventScheduleException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void createEvent_withoutDestination_throwsLocationException() {
        assertThatThrownBy(() -> service.createEvent("Hike", "desc", Category.SPORT, 10, ownerId,
                tomorrow(), LocalTime.of(10, 0), LocalTime.of(12, 0), null, null, null))
                .isInstanceOf(InvalidEventLocationException.class);
    }

    @Test
    void createEventLinkedToParche_whenMember_savesAndPublishes() {
        UUID parcheId = UUID.randomUUID();
        when(membershipRepository.exists(parcheId, ownerId)).thenReturn(true);

        Event result = service.createEventLinkedToParche("Hike", "desc", Category.SPORT, 10, parcheId, ownerId,
                tomorrow(), LocalTime.of(10, 0), LocalTime.of(12, 0), null, destination, null);

        verify(repository).save(any(Event.class));
        verify(publisher).publishEventLinkedToParche(eq(result.getEventId()), eq(parcheId), eq(ownerId));
    }

    @Test
    void createEventLinkedToParche_whenNotMember_throws() {
        UUID parcheId = UUID.randomUUID();
        when(membershipRepository.exists(parcheId, ownerId)).thenReturn(false);

        assertThatThrownBy(() -> service.createEventLinkedToParche("Hike", "desc", Category.SPORT, 10, parcheId, ownerId,
                tomorrow(), LocalTime.of(10, 0), LocalTime.of(12, 0), null, destination, null))
                .isInstanceOf(NotParcheMemberException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deleteEvent_byOwner_deletesAndPublishes() {
        Event event = existingEvent(10);
        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        service.deleteEvent(eventId, ownerId);

        verify(repository).delete(event);
        verify(publisher).publishEventDeleted(eq(eventId), any());
    }

    @Test
    void deleteEvent_byNonOwner_throws() {
        Event event = existingEvent(10);
        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> service.deleteEvent(eventId, UUID.randomUUID()))
                .isInstanceOf(NotEventOwnerException.class);
        verify(repository, never()).delete(any());
    }

    // ---- ManageUserEventCase ----

    @Test
    void joinEvent_addsParticipant() {
        Event event = existingEvent(10);
        when(repository.findById(eventId)).thenReturn(Optional.of(event));
        UUID joiner = UUID.randomUUID();

        service.joinEvent(joiner, eventId);

        assertThat(event.hasParticipant(joiner)).isTrue();
        verify(repository).save(event);
    }

    @Test
    void joinEvent_whenAlreadyParticipant_isNoOp() {
        Event event = existingEvent(10);
        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        service.joinEvent(ownerId, eventId); // owner already inscribed

        verify(repository, never()).save(any());
    }

    @Test
    void joinEvent_whenFull_throws() {
        Event event = existingEvent(1); // owner fills capacity
        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> service.joinEvent(UUID.randomUUID(), eventId))
                .isInstanceOf(EventIsFullException.class);
    }

    @Test
    void removeUser_selfLeave_removesParticipant() {
        Event event = existingEvent(10);
        UUID member = UUID.randomUUID();
        event.addParticipant(member);
        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        service.removeUserFromEvent(member, eventId, member);

        assertThat(event.hasParticipant(member)).isFalse();
        verify(repository).save(event);
    }

    @Test
    void removeUser_byOwner_kicksMember() {
        Event event = existingEvent(10);
        UUID member = UUID.randomUUID();
        event.addParticipant(member);
        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        service.removeUserFromEvent(member, eventId, ownerId);

        assertThat(event.hasParticipant(member)).isFalse();
        verify(repository).save(event);
    }

    @Test
    void removeUser_byNonOwnerNonSelf_throws() {
        Event event = existingEvent(10);
        UUID member = UUID.randomUUID();
        event.addParticipant(member);
        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> service.removeUserFromEvent(member, eventId, UUID.randomUUID()))
                .isInstanceOf(NotEventOwnerException.class);
    }

    @Test
    void removeUser_owner_cannotRemoveItself() {
        Event event = existingEvent(10);
        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> service.removeUserFromEvent(ownerId, eventId, ownerId))
                .isInstanceOf(CannotRemoveOwnerException.class);
    }

    // ---- EventLifecycleCase ----

    @Test
    void startDueEvents_marksStartedAndPublishes() {
        Event event = new Event(eventId, "Hike", "desc", Category.SPORT, 10, ownerId,
                LocalDate.now().minusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
        event.setDestination(destination);
        when(repository.findStartableCandidates(any())).thenReturn(List.of(event));

        service.startDueEvents();

        assertThat(event.isStarted()).isTrue();
        verify(repository).save(event);
        verify(publisher).publishEventStarted(eq(eventId), any());
    }

    @Test
    void endDueEvents_marksFinishedAndPublishes() {
        Event event = new Event(eventId, "Hike", "desc", Category.SPORT, 10, ownerId,
                LocalDate.now().minusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
        when(repository.findFinishableCandidates(any())).thenReturn(List.of(event));

        service.endDueEvents();

        assertThat(event.isFinished()).isTrue();
        verify(repository).save(event);
        verify(publisher).publishEventEnded(eventId);
    }

    // ---- EventQueryCase ----

    @Test
    void getEventById_whenFound_returns() {
        Event event = existingEvent(10);
        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        assertThat(service.getEventById(eventId)).isSameAs(event);
    }

    @Test
    void getEventById_whenMissing_throws() {
        when(repository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEventById(eventId))
                .isInstanceOf(EventNotFoundException.class);
    }

    // ---- SpecialQueriesFilterCases ----

    @Test
    void filterByCategory_delegates() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Event> page = new PageImpl<>(List.of(existingEvent(10)));
        when(repository.findByCategory(Category.SPORT, pageable)).thenReturn(page);

        assertThat(service.filterByCategory(Category.SPORT, pageable)).isSameAs(page);
    }

    @Test
    void findByName_delegates() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Event> page = new PageImpl<>(List.of(existingEvent(10)));
        when(repository.findByNameContaining("hike", pageable)).thenReturn(page);

        assertThat(service.findByName("hike", pageable)).isSameAs(page);
    }

    @Test
    void filterByOpenSlots_delegates() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Event> page = new PageImpl<>(List.of(existingEvent(10)));
        when(repository.findWithOpenSlots(pageable)).thenReturn(page);

        assertThat(service.filterByOpenSlots(pageable)).isSameAs(page);
    }

    @Test
    void filterByDate_delegates() {
        Pageable pageable = PageRequest.of(0, 20);
        LocalDate date = tomorrow();
        Page<Event> page = new PageImpl<>(List.of(existingEvent(10)));
        when(repository.findByEventDate(date, pageable)).thenReturn(page);

        assertThat(service.filterByDate(date, pageable)).isSameAs(page);
    }
}
