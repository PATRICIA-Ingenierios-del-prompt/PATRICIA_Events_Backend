package ingprompt.patricia.events.application.service;

import ingprompt.patricia.events.application.port.in.EventLifecycleCase;
import ingprompt.patricia.events.application.port.in.EventMapQueryCase;
import ingprompt.patricia.events.application.port.in.EventQueryCase;
import ingprompt.patricia.events.application.port.in.ManageEventCase;
import ingprompt.patricia.events.application.port.in.ManageUserEventCase;
import ingprompt.patricia.events.application.port.in.SpecialQueriesFilterCases;
import ingprompt.patricia.events.application.port.out.EventPublisherOut;
import ingprompt.patricia.events.application.port.out.EventRepositoryOutPort;
import ingprompt.patricia.events.application.port.out.ParcheMembershipRepositoryOutPort;
import ingprompt.patricia.events.application.port.out.ParcheVisibilityRepositoryOutPort;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.exception.EventNotFoundException;
import ingprompt.patricia.events.domain.exception.NotEventOwnerException;
import ingprompt.patricia.events.domain.exception.NotParcheMemberException;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.domain.model.Location;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EventService implements ManageEventCase, ManageUserEventCase, EventQueryCase, EventLifecycleCase, SpecialQueriesFilterCases, EventMapQueryCase {
    private final EventRepositoryOutPort repositoryOutPort;
    private final EventPublisherOut eventPublisher;
    private final ParcheMembershipRepositoryOutPort membershipRepository;
    private final ParcheVisibilityRepositoryOutPort visibilityRepository;

    @Override
    @Transactional
    public Event createEvent(String name, String description, Category category, int maxCapacity, UUID ownerId, LocalDate eventDate, LocalTime startTime, LocalTime endTime, Location meetingPoint, Location destination, String pictureUrl) {
        Event event = new Event(UUID.randomUUID(), name, description, category, maxCapacity, ownerId, eventDate, startTime, endTime);
        event.setMeetingPoint(meetingPoint);
        event.setDestination(destination);
        event.setPictureUrl(pictureUrl);
        event.validateSchedule(LocalDateTime.now());
        event.validateLocations();
        repositoryOutPort.save(event);
        eventPublisher.publishEventCreated(event.getEventId(), event.getName(), ownerId, false);
        return event;
    }

    @Override
    @Transactional
    public Event createEventLinkedToParche(String name, String description, Category category, int maxCapacity, UUID parcheId, UUID ownerId, LocalDate eventDate, LocalTime startTime, LocalTime endTime, Location meetingPoint, Location destination, String pictureUrl) {
        if (!membershipRepository.exists(parcheId, ownerId)) {
            throw new NotParcheMemberException(ownerId, parcheId);
        }

        Event event = new Event(UUID.randomUUID(), name, description, category, maxCapacity, parcheId, ownerId, eventDate, startTime, endTime);
        event.setMeetingPoint(meetingPoint);
        event.setDestination(destination);
        event.setPictureUrl(pictureUrl);
        event.validateSchedule(LocalDateTime.now());
        event.validateLocations();
        repositoryOutPort.save(event);

        String parcheName = visibilityRepository.findNameById(parcheId).orElse("");
        Set<UUID> memberIds = membershipRepository.findUserIdsByParcheId(parcheId);
        // userId (creator/linker) is required by Parches MS to authorize the link — the
        // Notification MS ignores it via Jackson INFERRED type precedence.
        eventPublisher.publishEventLinkedToParche(event.getEventId(), event.getName(), parcheId, parcheName, ownerId, memberIds);
        eventPublisher.publishEventCreated(event.getEventId(), event.getName(), ownerId, true);
        return event;
    }

    @Override
    @Transactional
    public void deleteEvent(UUID eventId, UUID ownerId) {
        Event event = repositoryOutPort.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (!event.isOwnedBy(ownerId)) {
            throw new NotEventOwnerException(ownerId, eventId);
        }
        UUID parcheId = event.getParcheId();
        repositoryOutPort.delete(event);
        eventPublisher.publishEventDeleted(eventId, parcheId);
    }

    @Override
    @Transactional
    public void joinEvent(UUID userId, UUID eventId) {
        Event event = repositoryOutPort.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (event.hasParticipant(userId)) {
            return;
        }
        if (event.isLinkedToParche() && !membershipRepository.exists(event.getParcheId(), userId)) {
            throw new NotParcheMemberException(userId, event.getParcheId());
        }
        event.addParticipant(userId);
        repositoryOutPort.save(event);
    }

    @Override
    @Transactional
    public void removeUserFromEvent(UUID userId, UUID eventId, UUID requesterId) {
        Event event = repositoryOutPort.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        boolean selfLeave = userId.equals(requesterId);
        if (!selfLeave && !event.isOwnedBy(requesterId)) {
            throw new NotEventOwnerException(requesterId, eventId);
        }
        event.removeParticipant(userId);
        repositoryOutPort.save(event);
    }

    @Override
    @Transactional
    public void startDueEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime trackingHorizon = now.plus(Event.TRACKING_LEAD_TIME);
        for (Event event : repositoryOutPort.findStartableCandidates(trackingHorizon.toLocalDate())) {
            if (!event.isStarted() && !event.startsAt().isAfter(trackingHorizon)) {
                event.markStarted();
                repositoryOutPort.save(event);
                eventPublisher.publishEventStarted(event.getEventId(), event.getUsersInscribed());
            }
        }
    }

    @Override
    @Transactional
    public void endDueEvents() {
        LocalDateTime now = LocalDateTime.now();
        for (Event event : repositoryOutPort.findFinishableCandidates(now.toLocalDate())) {
            if (!event.isFinished() && !event.endsAt().isAfter(now)) {
                event.markFinished();
                repositoryOutPort.save(event);
                eventPublisher.publishEventEnded(event.getEventId());
            }
        }
    }

    @Override
    public Event getEventById(UUID eventId) {
        return repositoryOutPort.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Event> filterByCategory(Category category, Pageable pageable) {
        return repositoryOutPort.findByCategory(category, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Event> findByName(String name, Pageable pageable) {
        return repositoryOutPort.findByNameContaining(name, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Event> filterByDate(LocalDate date, Pageable pageable) {
        return repositoryOutPort.findByEventDate(date, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Event> publicOpenEvents(Pageable pageable) {
        return repositoryOutPort.findPublicOpenEvents(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Event> myParcheOpenEvents(UUID userId, Pageable pageable) {
        Set<UUID> parcheIds = membershipRepository.findParcheIdsByUser(userId);
        if (parcheIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return repositoryOutPort.findOpenEventsForParches(parcheIds, pageable);
    }
}
