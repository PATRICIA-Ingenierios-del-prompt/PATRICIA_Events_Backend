package ingprompt.patricia.events.application.port.out;

import java.util.Optional;
import java.util.UUID;

/**
 * Local read model of Parches, fed by inbound `parche.created` / `parche.deleted`
 * events. Holds just enough state for map-visibility filtering and for enriching
 * outbound `event.linked.to.parche` payloads (the parche name).
 */
public interface ParcheVisibilityRepositoryOutPort {
    void save(UUID parcheId, String name, String visibility);
    void deleteByParcheId(UUID parcheId);
    Optional<String> findNameById(UUID parcheId);
}
