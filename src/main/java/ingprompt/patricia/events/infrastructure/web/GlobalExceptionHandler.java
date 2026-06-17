package ingprompt.patricia.events.infrastructure.web;

import ingprompt.patricia.events.domain.exception.CannotRemoveOwnerException;
import ingprompt.patricia.events.domain.exception.EventIsFullException;
import ingprompt.patricia.events.domain.exception.EventNotFoundException;
import ingprompt.patricia.events.domain.exception.InvalidEventScheduleException;
import ingprompt.patricia.events.domain.exception.NotEventOwnerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EventNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NotEventOwnerException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(NotEventOwnerException ex) {
        return error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({EventIsFullException.class, CannotRemoveOwnerException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidEventScheduleException.class)
    public ResponseEntity<Map<String, String>> handleBadSchedule(InvalidEventScheduleException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
