package ingprompt.patricia.events.domain.model;

import ingprompt.patricia.events.domain.enums.ReportType;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Report {
    private final UUID reportId;
    private final UUID eventId;
    private final UUID reporterId;
    private final Instant reportedAt;
    private final ReportType reportType;
    private final String description;

    private Report(UUID reportId, UUID eventId, UUID reporterId, Instant reportedAt, ReportType reportType, String description) {
        this.reportId = reportId;
        this.eventId = eventId;
        this.reporterId = reporterId;
        this.reportedAt = reportedAt;
        this.reportType = reportType;
        this.description = description;
    }

    public static Report create(UUID eventId, UUID reporterId, ReportType reportType, String description) {
        return new Report(UUID.randomUUID(), eventId, reporterId, Instant.now(), reportType, description);
    }

    public static Report rehydrate(UUID reportId, UUID eventId, UUID reporterId, Instant reportedAt,
                                   ReportType reportType, String description) {
        return new Report(reportId, eventId, reporterId, reportedAt, reportType, description);
    }
}
