package ingprompt.patricia.events.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreatedEvent {
    private UUID sourceEventId;
    private UUID eventId;
    private String name;
    private UUID ownerId;
    private boolean linkedToParche;
}
