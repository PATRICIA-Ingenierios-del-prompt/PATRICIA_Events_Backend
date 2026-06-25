package ingprompt.patricia.events.infrastructure.persistence.postgre;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
    List<EventEntity> findByStartedFalseAndEventDateLessThanEqual(LocalDate date);
    List<EventEntity> findByFinishedFalseAndEventDateLessThanEqual(LocalDate date);

    Page<EventEntity> findByCategory(Category category, Pageable pageable);
    Page<EventEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<EventEntity> findByEventDate(LocalDate date, Pageable pageable);

    @Query(value = "SELECT e FROM EventEntity e WHERE SIZE(e.usersInscribed) < e.maxCapacity",
            countQuery = "SELECT COUNT(e) FROM EventEntity e WHERE SIZE(e.usersInscribed) < e.maxCapacity")
    Page<EventEntity> findWithOpenSlots(Pageable pageable);
}
