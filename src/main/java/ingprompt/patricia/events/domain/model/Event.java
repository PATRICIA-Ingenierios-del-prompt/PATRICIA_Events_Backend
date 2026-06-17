package ingprompt.patricia.events.domain.model;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.exception.CannotRemoveOwnerException;
import ingprompt.patricia.events.domain.exception.EventIsFullException;
import ingprompt.patricia.events.domain.exception.InvalidEventScheduleException;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class Event {
    public static final Duration MIN_LEAD_TIME = Duration.ofMinutes(30);
    public static final Duration MAX_DURATION = Duration.ofHours(24);

    private UUID eventId;
    private String name;
    private String description;
    private UUID ownerId;
    private Category category;
    private int maxCapacity;
    private Set<UUID> usersInscribed;

    private UUID parcheId;

    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public Event(UUID eventId, String name, String description, Category category, int maxCapacity, UUID ownerId, LocalDate eventDate, LocalTime startTime, LocalTime endTime) {
        this(eventId, name, description, category, maxCapacity, null, ownerId, eventDate, startTime, endTime);
    }

    public Event(UUID eventId, String name, String description, Category category, int maxCapacity, UUID parcheId, UUID ownerId, LocalDate eventDate, LocalTime startTime, LocalTime endTime) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.maxCapacity = maxCapacity;
        this.parcheId = parcheId;
        this.ownerId = ownerId;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.usersInscribed = new HashSet<>();
        this.usersInscribed.add(ownerId);
    }

    public void validateSchedule(LocalDateTime createdAt) {
        if (eventDate == null || startTime == null || endTime == null) {
            throw new InvalidEventScheduleException("Event date, start time and end time are required");
        }
        if (eventDate.isBefore(createdAt.toLocalDate())) {
            throw new InvalidEventScheduleException("Event date cannot be in the past");
        }
        LocalDateTime startsAt = startsAt();
        Duration leadTime = Duration.between(createdAt, startsAt);
        if (leadTime.isNegative() || leadTime.compareTo(MIN_LEAD_TIME) < 0) {
            throw new InvalidEventScheduleException("Event must start at least " + MIN_LEAD_TIME.toMinutes() + " minutes after creation");
        }
        if (startTime.equals(endTime)) {
            throw new InvalidEventScheduleException("Start time and end time must differ");
        }
        Duration duration = Duration.between(startsAt, endsAt());
        if (duration.compareTo(MAX_DURATION) > 0) {
            throw new InvalidEventScheduleException("Event duration cannot exceed " + MAX_DURATION.toHours() + " hours");
        }
    }

    public LocalDateTime startsAt() {
        return LocalDateTime.of(eventDate, startTime);
    }
    public LocalDateTime endsAt() {
        LocalDate endDate = endTime.isAfter(startTime) ? eventDate : eventDate.plusDays(1);
        return LocalDateTime.of(endDate, endTime);
    }

    public void addParticipant(UUID participantId) {
        if (hasParticipant(participantId)) {
            return;
        }
        if (isFull()) {
            throw new EventIsFullException(this.eventId);
        }
        usersInscribed.add(participantId);
    }

    public void removeParticipant(UUID participantId) {
        if (isOwnedBy(participantId)) {
            throw new CannotRemoveOwnerException(participantId, this.eventId);
        }
        usersInscribed.remove(participantId);
    }

    public boolean isFull() {
        return this.usersInscribed.size() >= this.maxCapacity;
    }

    public boolean isLinkedToParche() {
        return parcheId != null;
    }

    public boolean isOwnedBy(UUID userId) {
        return this.ownerId.equals(userId);
    }

    public boolean hasParticipant(UUID userId) {
        return this.usersInscribed.contains(userId);
    }
}
