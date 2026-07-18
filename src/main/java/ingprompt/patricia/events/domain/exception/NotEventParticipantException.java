package ingprompt.patricia.events.domain.exception;

public class NotEventParticipantException extends RuntimeException {
    public NotEventParticipantException() {
        super("Debes estar inscrito en el evento para poder reportarlo.");
    }
}
