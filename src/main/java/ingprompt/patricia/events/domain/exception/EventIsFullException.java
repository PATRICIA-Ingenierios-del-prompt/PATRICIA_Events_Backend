package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class EventIsFullException extends RuntimeException {
    public EventIsFullException(UUID eventId) {
        super("El evento ya no tiene cupos disponibles.");
    }
}
