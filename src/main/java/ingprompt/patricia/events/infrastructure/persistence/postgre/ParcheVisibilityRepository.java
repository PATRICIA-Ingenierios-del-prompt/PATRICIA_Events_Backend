package ingprompt.patricia.events.infrastructure.persistence.postgre;

import ingprompt.patricia.events.infrastructure.persistence.entity.ParcheVisibilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParcheVisibilityRepository extends JpaRepository<ParcheVisibilityEntity, UUID> {
}
