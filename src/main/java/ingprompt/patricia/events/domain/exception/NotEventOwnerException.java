package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class NotEventOwnerException extends RuntimeException {
    public NotEventOwnerException(UUID userId, UUID eventId) {
        super("User "+userId+" is not the owner of event "+eventId);
    }
}
