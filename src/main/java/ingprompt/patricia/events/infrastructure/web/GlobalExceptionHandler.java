package ingprompt.patricia.events.infrastructure.web;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import ingprompt.patricia.events.domain.exception.CannotRemoveOwnerException;
import ingprompt.patricia.events.domain.exception.EventIsFullException;
import ingprompt.patricia.events.domain.exception.EventNotFoundException;
import ingprompt.patricia.events.domain.exception.InvalidEventLocationException;
import ingprompt.patricia.events.domain.exception.InvalidEventScheduleException;
import ingprompt.patricia.events.domain.exception.InvalidPictureUploadException;
import ingprompt.patricia.events.domain.exception.NotEventOwnerException;
import ingprompt.patricia.events.domain.exception.NotEventParticipantException;
import ingprompt.patricia.events.domain.exception.NotParcheMemberException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EventNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({NotEventOwnerException.class, NotParcheMemberException.class, NotEventParticipantException.class})
    public ResponseEntity<Map<String, String>> handleForbidden(RuntimeException ex) {
        return error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({EventIsFullException.class, CannotRemoveOwnerException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({InvalidEventScheduleException.class, InvalidEventLocationException.class, InvalidPictureUploadException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Catches malformed JSON fields — e.g. a date like "2024-13-45" or a time
     * like "25:99" that Jackson cannot parse into LocalDate / LocalTime.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            Class<?> targetType = ife.getTargetType();
            if (targetType != null) {
                if (LocalDate.class.isAssignableFrom(targetType)) {
                    return error(HttpStatus.BAD_REQUEST,
                        "El formato de fecha no es válido. Usa el formato yyyy-MM-dd (ej. 2025-07-20).");
                }
                if (LocalTime.class.isAssignableFrom(targetType)) {
                    return error(HttpStatus.BAD_REQUEST,
                        "El formato de hora no es válido. Usa el formato HH:mm (ej. 14:30).");
                }
            }
        }
        return error(HttpStatus.BAD_REQUEST,
            "El cuerpo de la solicitud tiene un formato incorrecto. Revisa los datos e intenta de nuevo.");
    }

    /**
     * Catches type mismatches in @RequestParam or @PathVariable —
     * e.g. ?date=not-a-date.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType != null && LocalDate.class.isAssignableFrom(requiredType)) {
            return error(HttpStatus.BAD_REQUEST,
                "La fecha '" + ex.getValue() + "' no es válida. Usa el formato yyyy-MM-dd (ej. 2025-07-20).");
        }
        return error(HttpStatus.BAD_REQUEST,
            "El parámetro '" + ex.getName() + "' tiene un valor inválido: " + ex.getValue());
    }

    /**
     * Safety net: anything not handled above becomes a 500 with a friendly message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        // Log at ERROR level in production — here we just surface a safe message.
        return error(HttpStatus.INTERNAL_SERVER_ERROR,
            "Ocurrió un error inesperado en el servidor. Intenta de nuevo en un momento.");
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
