package ingprompt.patricia.events.domain.exception;

import java.util.UUID;

public class NotParcheMemberException extends RuntimeException {
    public NotParcheMemberException(UUID userId, UUID parcheId) {
        super("No eres miembro de este parche.");
    }
}
