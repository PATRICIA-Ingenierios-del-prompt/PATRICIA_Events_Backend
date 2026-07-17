package ingprompt.patricia.events.domain.exception;

public class CannotRemoveOwnerException extends RuntimeException {
    public CannotRemoveOwnerException() {
        super("El creador del evento no puede ser eliminado de su propio evento.");
    }
}
