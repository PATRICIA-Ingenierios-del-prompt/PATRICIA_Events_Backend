package ingprompt.patricia.events.domain.exception;

public class InvalidEventScheduleException extends RuntimeException {
    public InvalidEventScheduleException(String reason) {
        super(reason);
    }
}
