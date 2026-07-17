package ingprompt.patricia.events.domain.model;

import ingprompt.patricia.events.domain.enums.ReportType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReportTest {

    @Test
    void create_generatesIdAndTimestamp() {
        UUID eventId = UUID.randomUUID();
        UUID reporterId = UUID.randomUUID();

        Report report = Report.create(eventId, reporterId, ReportType.THEFT, "help");

        assertThat(report.getReportId()).isNotNull();
        assertThat(report.getReportedAt()).isNotNull();
        assertThat(report.getEventId()).isEqualTo(eventId);
        assertThat(report.getReporterId()).isEqualTo(reporterId);
        assertThat(report.getReportType()).isEqualTo(ReportType.THEFT);
        assertThat(report.getDescription()).isEqualTo("help");
    }

    @Test
    void rehydrate_preservesAllFields() {
        UUID reportId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID reporterId = UUID.randomUUID();
        Instant reportedAt = Instant.parse("2026-01-01T00:00:00Z");

        Report report = Report.rehydrate(reportId, eventId, reporterId, reportedAt, ReportType.MEDICAL_EMERGENCY, "note");

        assertThat(report.getReportId()).isEqualTo(reportId);
        assertThat(report.getEventId()).isEqualTo(eventId);
        assertThat(report.getReporterId()).isEqualTo(reporterId);
        assertThat(report.getReportedAt()).isEqualTo(reportedAt);
        assertThat(report.getReportType()).isEqualTo(ReportType.MEDICAL_EMERGENCY);
        assertThat(report.getDescription()).isEqualTo("note");
    }
}
