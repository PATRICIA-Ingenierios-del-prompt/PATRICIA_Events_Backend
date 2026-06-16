package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class EventIsFullException extends RuntimeException {
    public EventIsFullException(UUID eventId) {
        super("Event "+ eventId + "is full");
    }
}
