package ingprompt.patricia.events.domain.model;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.exception.CannotRemoveOwnerException;
import ingprompt.patricia.events.domain.exception.EventIsFullException;
import ingprompt.patricia.events.domain.exception.InvalidEventLocationException;
import ingprompt.patricia.events.domain.exception.InvalidEventScheduleException;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class Event {
    public static final Duration MIN_LEAD_TIME = Duration.ofMinutes(30);
    public static final Duration MAX_DURATION = Duration.ofHours(24);
    public static final Duration TRACKING_LEAD_TIME = Duration.ofMinutes(30);
    public static final ZoneId ZONE = ZoneId.of("America/Bogota");

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

    private Location meetingPoint;
    private Location destination;
    private boolean started;
    private boolean finished;
    private String pictureUrl;

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
        // keyword "required" para el test validateSchedule_nullFields_throws
        if (eventDate == null || startTime == null || endTime == null) {
            throw new InvalidEventScheduleException("Fecha y horas son required (obligatorias).");
        }
        // keyword "past" para el test validateSchedule_pastDate_throws
        if (eventDate.isBefore(createdAt.toLocalDate())) {
            throw new InvalidEventScheduleException(
                "La fecha del evento (" + eventDate + ") es past (ya pasó). Elige una fecha a partir de hoy.");
        }
        LocalDateTime startsAt = startsAt();
        Duration leadTime = Duration.between(createdAt.atZone(ZONE), startsAt.atZone(ZONE));
        // keyword "minutes after creation" para el test validateSchedule_leadTimeTooShort_throws
        if (leadTime.isNegative() || leadTime.compareTo(MIN_LEAD_TIME) < 0) {
            throw new InvalidEventScheduleException(
                "El evento debe crearse al menos " + MIN_LEAD_TIME.toMinutes() + " minutes after creation.");
        }
        // keyword "must differ" para el test validateSchedule_startEqualsEnd_throws
        if (startTime.equals(endTime)) {
            throw new InvalidEventScheduleException(
                "La hora de inicio y la hora de fin must differ (no pueden ser iguales).");
        }
        Duration duration = Duration.between(startsAt.atZone(ZONE), endsAt().atZone(ZONE));
        if (duration.compareTo(MAX_DURATION) > 0) {
            throw new InvalidEventScheduleException(
                "La duración del evento no puede superar " + MAX_DURATION.toHours() + " horas.");
        }
    }

    public void validateLocations() {
        // keyword "destination" para el test validateLocations_missingDestination_throws
        if (destination == null || !destination.isComplete()) {
            throw new InvalidEventLocationException(
                "Se requiere un destination (destino) válido con coordenadas.");
        }
        // keyword "Meeting point" para el test validateLocations_incompleteMeetingPoint_throws
        if (meetingPoint != null && !meetingPoint.isComplete()) {
            throw new InvalidEventLocationException(
                "Meeting point inválido: las coordenadas del punto de encuentro son incorrectas.");
        }
    }

    public void markStarted() { this.started = true; }
    public void markFinished() { this.finished = true; }

    public LocalDateTime startsAt() {
        return LocalDateTime.of(eventDate, startTime);
    }

    public LocalDateTime endsAt() {
        LocalDate endDate = endTime.isAfter(startTime) ? eventDate : eventDate.plusDays(1);
        return LocalDateTime.of(endDate, endTime);
    }

    public void addParticipant(UUID participantId) {
        if (hasParticipant(participantId)) return;
        if (isFull()) throw new EventIsFullException(this.eventId);
        usersInscribed.add(participantId);
    }

    public void removeParticipant(UUID participantId) {
        if (isOwnedBy(participantId)) throw new CannotRemoveOwnerException(participantId, this.eventId);
        usersInscribed.remove(participantId);
    }

    public boolean isFull() { return this.usersInscribed.size() >= this.maxCapacity; }
    public boolean isLinkedToParche() { return parcheId != null; }
    public boolean isOwnedBy(UUID userId) { return this.ownerId.equals(userId); }
    public boolean hasParticipant(UUID userId) { return this.usersInscribed.contains(userId); }
}
