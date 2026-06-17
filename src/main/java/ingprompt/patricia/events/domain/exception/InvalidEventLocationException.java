package ingprompt.patricia.events.domain.exception;

public class InvalidEventLocationException extends RuntimeException {
    public InvalidEventLocationException(String reason) {
        super(reason);
    }
}
