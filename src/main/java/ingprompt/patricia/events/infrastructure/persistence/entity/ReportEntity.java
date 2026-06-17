package ingprompt.patricia.events.infrastructure.persistence.entity;

import ingprompt.patricia.events.domain.enums.ReportType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Data
public class ReportEntity {
    @Id
    private UUID reportId;

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private UUID reporterId;

    @Column(nullable = false)
    private Instant reportedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @Column(length = 2000)
    private String description;
}
