package ingprompt.patricia.events.infrastructure.web.dto.request;

import ingprompt.patricia.events.domain.enums.ReportType;
import lombok.Data;

@Data
public class CreateReportRequest {
    private ReportType reportType;
    private String description;
}
