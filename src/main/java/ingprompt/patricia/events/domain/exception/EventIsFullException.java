package ingprompt.patricia.events.domain.exception;

public class EventIsFullException extends RuntimeException {
    public EventIsFullException() {
        super("El evento ya no tiene cupos disponibles.");
    }
}
