package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class NotEventParticipantException extends RuntimeException {
    public NotEventParticipantException(UUID userId, UUID eventId) {
        super("User " + userId + " is not a participant of event " + eventId + " and cannot report on it");
    }
}
