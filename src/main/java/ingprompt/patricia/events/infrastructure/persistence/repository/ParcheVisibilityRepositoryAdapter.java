package ingprompt.patricia.events.infrastructure.persistence.repository;

import ingprompt.patricia.events.application.port.out.ParcheVisibilityRepositoryOutPort;
import ingprompt.patricia.events.infrastructure.persistence.entity.ParcheVisibilityEntity;
import ingprompt.patricia.events.infrastructure.persistence.postgre.ParcheVisibilityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class ParcheVisibilityRepositoryAdapter implements ParcheVisibilityRepositoryOutPort {
    private final ParcheVisibilityRepository repository;

    @Override
    public void save(UUID parcheId, String visibility) {
        repository.save(new ParcheVisibilityEntity(parcheId, visibility));
    }

    @Override
    public void deleteByParcheId(UUID parcheId) {
        repository.deleteById(parcheId);
    }
}
