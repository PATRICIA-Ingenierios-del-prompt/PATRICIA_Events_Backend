package ingprompt.patricia.events.application.service;

import ingprompt.patricia.events.application.port.in.EventLifecycleCase;
import ingprompt.patricia.events.application.port.in.EventQueryCase;
import ingprompt.patricia.events.application.port.in.ManageEventCase;
import ingprompt.patricia.events.application.port.in.ManageUserEventCase;
import ingprompt.patricia.events.application.port.out.EventPublisherOut;
import ingprompt.patricia.events.application.port.out.EventRepositoryOutPort;
import ingprompt.patricia.events.application.port.out.ParcheMembershipRepositoryOutPort;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.exception.EventNotFoundException;
import ingprompt.patricia.events.domain.exception.NotEventOwnerException;
import ingprompt.patricia.events.domain.exception.NotParcheMemberException;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.domain.model.Location;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EventService implements ManageEventCase, ManageUserEventCase, EventQueryCase, EventLifecycleCase {
    private final EventRepositoryOutPort repositoryOutPort;
    private final EventPublisherOut eventPublisher;
    private final ParcheMembershipRepositoryOutPort membershipRepository;

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
        eventPublisher.publishEventLinkedToParche(event.getEventId(), parcheId, ownerId);
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
}
