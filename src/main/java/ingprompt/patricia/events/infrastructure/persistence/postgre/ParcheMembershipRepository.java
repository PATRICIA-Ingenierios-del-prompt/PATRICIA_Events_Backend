package ingprompt.patricia.events.infrastructure.persistence.postgre;

import ingprompt.patricia.events.infrastructure.persistence.entity.ParcheMembershipEntity;
import ingprompt.patricia.events.infrastructure.persistence.entity.ParcheMembershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ParcheMembershipRepository extends JpaRepository<ParcheMembershipEntity, ParcheMembershipId> {
    @Modifying
    @Query("delete from ParcheMembershipEntity m where m.parcheId = :parcheId")
    void deleteAllByParcheId(@Param("parcheId") UUID parcheId);

    @Query("select m.parcheId from ParcheMembershipEntity m where m.userId = :userId")
    List<UUID> findParcheIdsByUserId(@Param("userId") UUID userId);

    @Query("select m.userId from ParcheMembershipEntity m where m.parcheId = :parcheId")
    List<UUID> findUserIdsByParcheId(@Param("parcheId") UUID parcheId);
}
