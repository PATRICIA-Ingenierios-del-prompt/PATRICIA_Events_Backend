package ingprompt.patricia.events.application.port.out;

import ingprompt.patricia.events.domain.model.Report;

public interface ReportRepositoryOutPort {
    void save(Report report);
}
