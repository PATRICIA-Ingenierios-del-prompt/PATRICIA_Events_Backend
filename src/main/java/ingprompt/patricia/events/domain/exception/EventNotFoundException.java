package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(UUID eventId) {
        super("Event "+eventId+" not found");
    }
}
