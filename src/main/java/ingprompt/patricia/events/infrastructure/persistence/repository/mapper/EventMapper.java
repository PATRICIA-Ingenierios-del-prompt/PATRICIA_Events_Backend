package ingprompt.patricia.events.infrastructure.persistence.repository.mapper;

import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.infrastructure.persistence.entity.EventEntity;

import java.util.HashSet;

public final class EventMapper {
    private EventMapper() {
    }

    public static EventEntity toEntity(Event event) {
        EventEntity entity = new EventEntity();
        entity.setEventId(event.getEventId());
        entity.setName(event.getName());
        entity.setDescription(event.getDescription());
        entity.setOwnerId(event.getOwnerId());
        entity.setCategory(event.getCategory());
        entity.setMaxCapacity(event.getMaxCapacity());
        entity.setParcheId(event.getParcheId());
        entity.setEventDate(event.getEventDate());
        entity.setStartTime(event.getStartTime());
        entity.setEndTime(event.getEndTime());
        entity.setMeetingPoint(event.getMeetingPoint());
        entity.setDestination(event.getDestination());
        entity.setStarted(event.isStarted());
        entity.setFinished(event.isFinished());
        entity.setUsersInscribed(new HashSet<>(event.getUsersInscribed()));
        return entity;
    }

    public static Event toDomain(EventEntity entity) {
        Event event = new Event(
                entity.getEventId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getMaxCapacity(),
                entity.getParcheId(),
                entity.getOwnerId(),
                entity.getEventDate(),
                entity.getStartTime(),
                entity.getEndTime()
        );
        event.setMeetingPoint(entity.getMeetingPoint());
        event.setDestination(entity.getDestination());
        event.setStarted(entity.isStarted());
        event.setFinished(entity.isFinished());
        event.setUsersInscribed(entity.getUsersInscribed() == null
                ? new HashSet<>()
                : new HashSet<>(entity.getUsersInscribed()));
        return event;
    }
}
