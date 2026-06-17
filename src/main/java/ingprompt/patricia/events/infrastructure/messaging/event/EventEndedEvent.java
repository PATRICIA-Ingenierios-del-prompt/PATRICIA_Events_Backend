package ingprompt.patricia.events.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Outbound: an event reached its end time — Location MS stops tracking. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventEndedEvent {
    private UUID eventId;
}
