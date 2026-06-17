package ingprompt.patricia.events.infrastructure.persistence.repository;

import ingprompt.patricia.events.application.port.out.ReportRepositoryOutPort;
import ingprompt.patricia.events.domain.model.Report;
import ingprompt.patricia.events.infrastructure.persistence.entity.ReportEntity;
import ingprompt.patricia.events.infrastructure.persistence.postgre.ReportRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReportRepositoryAdapter implements ReportRepositoryOutPort {
    private final ReportRepository postgreRepository;

    @Override
    public void save(Report report) {
        ReportEntity entity = new ReportEntity();
        entity.setReportId(report.getReportId());
        entity.setEventId(report.getEventId());
        entity.setReporterId(report.getReporterId());
        entity.setReportedAt(report.getReportedAt());
        entity.setReportType(report.getReportType());
        entity.setDescription(report.getDescription());
        postgreRepository.save(entity);
    }
}
