package ingprompt.patricia.events.domain.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException() {
        super("No se encontró el evento. Puede que haya sido eliminado.");
    }
}
