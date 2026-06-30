package ingprompt.patricia.events.application.port.out;

import java.util.UUID;

public interface ParcheVisibilityRepositoryOutPort {
    void save(UUID parcheId, String visibility);
    void deleteByParcheId(UUID parcheId);
}
