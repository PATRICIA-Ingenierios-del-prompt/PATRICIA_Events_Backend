package ingprompt.patricia.events.infrastructure.persistence.postgre;

import ingprompt.patricia.events.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
    List<EventEntity> findByStartedFalseAndEventDateLessThanEqual(LocalDate date);

    List<EventEntity> findByFinishedFalseAndEventDateLessThanEqual(LocalDate date);
}
