package ingprompt.patricia.events.application.port.out;

import java.util.UUID;

public interface EventPublisherOut {
    void publishEventLinkedToParche(UUID eventId, UUID parcheId, UUID userId);
    void publishEventDeleted(UUID eventId, UUID parcheId);
}
