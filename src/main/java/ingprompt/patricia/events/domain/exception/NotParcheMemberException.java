package ingprompt.patricia.events.domain.exception;

public class NotParcheMemberException extends RuntimeException {
    public NotParcheMemberException() {
        super("No eres miembro de este parche.");
    }
}
