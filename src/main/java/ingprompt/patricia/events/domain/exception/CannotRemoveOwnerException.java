package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class CannotRemoveOwnerException extends RuntimeException {
    public CannotRemoveOwnerException(UUID userId, UUID eventId) {
        super("El creador del evento no puede ser eliminado de su propio evento.");
    }
}
