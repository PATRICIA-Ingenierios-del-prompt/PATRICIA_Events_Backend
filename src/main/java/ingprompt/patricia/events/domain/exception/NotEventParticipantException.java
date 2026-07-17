package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class NotEventParticipantException extends RuntimeException {
    public NotEventParticipantException(UUID userId, UUID eventId) {
        super("Debes estar inscrito en el evento para poder reportarlo.");
    }
}
