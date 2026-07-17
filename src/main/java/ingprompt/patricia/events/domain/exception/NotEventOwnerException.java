package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class NotEventOwnerException extends RuntimeException {
    public NotEventOwnerException(UUID userId, UUID eventId) {
        super("Solo el creador del evento puede realizar esta acción.");
    }
}
