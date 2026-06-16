package ingprompt.patricia.events.domain.model;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.exception.CannotRemoveOwnerException;
import ingprompt.patricia.events.domain.exception.EventIsFullException;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class Event {
    private UUID eventId;
    private String name;
    private String description;
    private UUID ownerId;
    private Category category;
    private int maxCapacity;
    private Set<UUID> usersInscribed;

    private UUID parcheId;

    public Event(UUID eventId, String name, String description, Category category, int maxCapacity, UUID ownerId) {
        this(eventId, name, description, category, maxCapacity, null, ownerId);
    }

    public Event(UUID eventId, String name, String description, Category category, int maxCapacity, UUID parcheId, UUID ownerId) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.maxCapacity = maxCapacity;
        this.parcheId = parcheId;
        this.ownerId = ownerId;
        this.usersInscribed = new HashSet<>();
        this.usersInscribed.add(ownerId);
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
