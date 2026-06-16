package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class CannotRemoveOwnerException extends RuntimeException {
    public CannotRemoveOwnerException(UUID userId, UUID eventId) {
        super("Cannot remove user: "+userId+" from event "+eventId+" because that's it's owner");
    }
}
