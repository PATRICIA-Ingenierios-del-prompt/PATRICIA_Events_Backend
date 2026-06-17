package ingprompt.patricia.events.infrastructure.persistence.postgre;

import ingprompt.patricia.events.infrastructure.persistence.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {
}
