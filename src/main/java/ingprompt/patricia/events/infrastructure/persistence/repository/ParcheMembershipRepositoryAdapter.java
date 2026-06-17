package ingprompt.patricia.events.infrastructure.persistence.repository;

import ingprompt.patricia.events.application.port.out.ParcheMembershipRepositoryOutPort;
import ingprompt.patricia.events.infrastructure.persistence.entity.ParcheMembershipEntity;
import ingprompt.patricia.events.infrastructure.persistence.entity.ParcheMembershipId;
import ingprompt.patricia.events.infrastructure.persistence.postgre.ParcheMembershipRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class ParcheMembershipRepositoryAdapter implements ParcheMembershipRepositoryOutPort {
    private final ParcheMembershipRepository repository;

    @Override
    public void save(UUID parcheId, UUID userId) {
        // save() is upsert: if (parcheId, userId) already exists, it's a no-op overwrite.
        repository.save(new ParcheMembershipEntity(parcheId, userId));
    }

    @Override
    public void delete(UUID parcheId, UUID userId) {
        repository.deleteById(new ParcheMembershipId(parcheId, userId));
    }

    @Override
    public void deleteAllByParcheId(UUID parcheId) {
        repository.deleteAllByParcheId(parcheId);
    }

    @Override
    public boolean exists(UUID parcheId, UUID userId) {
        return repository.existsById(new ParcheMembershipId(parcheId, userId));
    }
}
