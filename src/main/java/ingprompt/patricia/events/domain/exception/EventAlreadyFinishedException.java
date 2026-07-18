package ingprompt.patricia.events.domain.exception;

public class EventAlreadyFinishedException extends RuntimeException {
    public EventAlreadyFinishedException() {
        super("El evento ya finalizó; no es posible inscribirse.");
    }
}
