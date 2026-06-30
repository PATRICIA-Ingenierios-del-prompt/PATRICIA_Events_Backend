package ingprompt.patricia.events.application.port.in;

import java.util.Set;
import java.util.UUID;

public interface ParcheMembershipCase {
    void handleParcheCreated(UUID parcheId, UUID ownerId, String visibility);
    void addMember(UUID parcheId, UUID userId);
    void removeMember(UUID parcheId, UUID userId);
    void handleParcheDeleted(UUID parcheId, Set<UUID> eventIds);
}
