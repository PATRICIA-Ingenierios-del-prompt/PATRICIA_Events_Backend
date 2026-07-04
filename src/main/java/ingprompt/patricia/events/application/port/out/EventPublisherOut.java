package ingprompt.patricia.events.application.port.out;

import java.util.Set;
import java.util.UUID;

public interface EventPublisherOut {
    void publishEventCreated(UUID eventId, String name, UUID ownerId, boolean linkedToParche);
    void publishEventLinkedToParche(UUID eventId, String eventName, UUID parcheId, String parcheName, UUID userId, Set<UUID> memberIds);
    void publishEventDeleted(UUID eventId, UUID parcheId);
    void publishEventStarted(UUID eventId, Set<UUID> participants);
    void publishEventEnded(UUID eventId);
    void publishIncidentReported(UUID eventId, UUID reportId, UUID reporterId);
}
