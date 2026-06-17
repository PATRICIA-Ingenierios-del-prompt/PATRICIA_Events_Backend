package ingprompt.patricia.events.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStartedEvent {
    private UUID eventId;
    private Set<UUID> participants;
}
