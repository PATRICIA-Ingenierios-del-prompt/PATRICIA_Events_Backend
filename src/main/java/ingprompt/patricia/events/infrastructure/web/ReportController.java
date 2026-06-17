package ingprompt.patricia.events.infrastructure.web;

import ingprompt.patricia.events.application.port.in.ManageReportCase;
import ingprompt.patricia.events.domain.model.Report;
import ingprompt.patricia.events.infrastructure.web.dto.request.CreateReportRequest;
import ingprompt.patricia.events.infrastructure.web.dto.response.CreateReportResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events/{eventId}/reports")
@AllArgsConstructor
public class ReportController {
    private final ManageReportCase manageReportCase;

    @PostMapping
    public ResponseEntity<CreateReportResponse> createReport(@PathVariable UUID eventId, @RequestHeader("X-User-Id") UUID reporterId, @RequestBody CreateReportRequest request) {
        Report report = manageReportCase.createReport(eventId, reporterId, request.getReportType(), request.getDescription());
        return ResponseEntity.ok(CreateReportResponse.from(report));
    }
}
