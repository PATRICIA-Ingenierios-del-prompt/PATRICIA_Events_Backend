package ingprompt.patricia.events.infrastructure.web.dto.response;

import ingprompt.patricia.events.domain.enums.ReportType;
import ingprompt.patricia.events.domain.model.Report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportResponse {
    private UUID reportId;
    private UUID eventId;
    private UUID reporterId;
    private ReportType reportType;
    private Instant reportedAt;

    public static CreateReportResponse from(Report report) {
        return new CreateReportResponse(
                report.getReportId(),
                report.getEventId(),
                report.getReporterId(),
                report.getReportType(),
                report.getReportedAt());
    }
}
