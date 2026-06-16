package ingprompt.patricia.events.application.port.in;

import java.util.UUID;

public interface ManageUserEventCase {
    void joinEvent(UUID userId, UUID eventId);
    void removeUserFromEvent(UUID userId, UUID eventId, UUID requesterId);
}
