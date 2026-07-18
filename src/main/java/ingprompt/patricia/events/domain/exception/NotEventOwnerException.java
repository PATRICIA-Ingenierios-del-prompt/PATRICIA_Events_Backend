package ingprompt.patricia.events.domain.exception;

public class NotEventOwnerException extends RuntimeException {
    public NotEventOwnerException() {
        super("Solo el creador del evento puede realizar esta acción.");
    }
}
