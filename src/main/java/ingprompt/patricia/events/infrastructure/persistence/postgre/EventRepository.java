package ingprompt.patricia.events.infrastructure.persistence.postgre;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
    List<EventEntity> findByStartedFalseAndEventDateLessThanEqual(LocalDate date);
    List<EventEntity> findByFinishedFalseAndEventDateLessThanEqual(LocalDate date);

    String PUBLICLY_VISIBLE_OPEN =
            " (e.parcheId IS NULL OR e.parcheId IN "
            + "   (SELECT pv.parcheId FROM ParcheVisibilityEntity pv WHERE pv.visibility = 'PUBLIC')) "
            + " AND e.finished = false AND SIZE(e.usersInscribed) < e.maxCapacity ";

    // Public map.
    @Query(value = "SELECT e FROM EventEntity e WHERE " + PUBLICLY_VISIBLE_OPEN,
            countQuery = "SELECT COUNT(e) FROM EventEntity e WHERE " + PUBLICLY_VISIBLE_OPEN)
    Page<EventEntity> findPublicOpenEvents(Pageable pageable);

    // Public filters — same visibility/open/not-finished scope, narrowed further.
    @Query(value = "SELECT e FROM EventEntity e WHERE e.category = :category AND " + PUBLICLY_VISIBLE_OPEN,
            countQuery = "SELECT COUNT(e) FROM EventEntity e WHERE e.category = :category AND " + PUBLICLY_VISIBLE_OPEN)
    Page<EventEntity> findVisibleOpenByCategory(@Param("category") Category category, Pageable pageable);

    @Query(value = "SELECT e FROM EventEntity e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) AND " + PUBLICLY_VISIBLE_OPEN,
            countQuery = "SELECT COUNT(e) FROM EventEntity e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) AND " + PUBLICLY_VISIBLE_OPEN)
    Page<EventEntity> findVisibleOpenByName(@Param("name") String name, Pageable pageable);

    @Query(value = "SELECT e FROM EventEntity e WHERE e.eventDate = :date AND " + PUBLICLY_VISIBLE_OPEN,
            countQuery = "SELECT COUNT(e) FROM EventEntity e WHERE e.eventDate = :date AND " + PUBLICLY_VISIBLE_OPEN)
    Page<EventEntity> findVisibleOpenByDate(@Param("date") LocalDate date, Pageable pageable);

    // My-parches map: events of the given parches, not finished, with open slots.
    @Query(value = "SELECT e FROM EventEntity e WHERE e.parcheId IN :parcheIds AND e.finished = false AND SIZE(e.usersInscribed) < e.maxCapacity",
            countQuery = "SELECT COUNT(e) FROM EventEntity e WHERE e.parcheId IN :parcheIds AND e.finished = false AND SIZE(e.usersInscribed) < e.maxCapacity")
    Page<EventEntity> findOpenEventsForParches(@Param("parcheIds") Collection<UUID> parcheIds, Pageable pageable);
}
