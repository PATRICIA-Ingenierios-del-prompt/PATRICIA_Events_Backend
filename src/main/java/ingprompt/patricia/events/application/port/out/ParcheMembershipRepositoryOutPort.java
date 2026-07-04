package ingprompt.patricia.events.application.port.out;

import java.util.Set;
import java.util.UUID;

public interface ParcheMembershipRepositoryOutPort {
    void save(UUID parcheId, UUID userId);
    void delete(UUID parcheId, UUID userId);
    void deleteAllByParcheId(UUID parcheId);
    boolean exists(UUID parcheId, UUID userId);

    Set<UUID> findParcheIdsByUser(UUID userId);
    /** Members of a parche — used to enrich event.linked.to.parche with the recipient set. */
    Set<UUID> findUserIdsByParcheId(UUID parcheId);
}
