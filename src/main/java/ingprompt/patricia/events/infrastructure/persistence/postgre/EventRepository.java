package ingprompt.patricia.events.infrastructure.persistence.postgre;

import ingprompt.patricia.events.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
}
