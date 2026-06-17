package ingprompt.patricia.events.application.port.in;

import ingprompt.patricia.events.domain.enums.ReportType;
import ingprompt.patricia.events.domain.model.Report;

import java.util.UUID;

public interface ManageReportCase {
    Report createReport(UUID eventId, UUID reporterId, ReportType reportType, String description);
}
