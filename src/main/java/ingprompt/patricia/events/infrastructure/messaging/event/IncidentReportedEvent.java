package ingprompt.patricia.events.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Outbound: a participant reported an incident — Location MS secures the snapshot. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentReportedEvent {
    private UUID eventId;
    private UUID reportId;
    private UUID reporterId;
}
