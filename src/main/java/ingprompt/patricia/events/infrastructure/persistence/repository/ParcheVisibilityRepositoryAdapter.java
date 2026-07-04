package ingprompt.patricia.events.infrastructure.persistence.repository;

import ingprompt.patricia.events.application.port.out.ParcheVisibilityRepositoryOutPort;
import ingprompt.patricia.events.infrastructure.persistence.entity.ParcheVisibilityEntity;
import ingprompt.patricia.events.infrastructure.persistence.postgre.ParcheVisibilityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ParcheVisibilityRepositoryAdapter implements ParcheVisibilityRepositoryOutPort {
    private final ParcheVisibilityRepository repository;

    @Override
    public void save(UUID parcheId, String name, String visibility) {
        repository.save(new ParcheVisibilityEntity(parcheId, name, visibility));
    }

    @Override
    public void deleteByParcheId(UUID parcheId) {
        repository.deleteById(parcheId);
    }

    @Override
    public Optional<String> findNameById(UUID parcheId) {
        return repository.findById(parcheId).map(ParcheVisibilityEntity::getName);
    }
}
