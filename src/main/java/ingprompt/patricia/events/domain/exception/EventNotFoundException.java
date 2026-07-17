package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(UUID eventId) {
        super("No se encontró el evento. Puede que haya sido eliminado.");
    }
}
