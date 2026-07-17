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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

/**
 * Extiende ResponseEntityExceptionHandler para que Spring MVC siga manejando
 * sus propias excepciones (MissingRequestHeaderException, etc.) con el status
 * correcto (400), y nosotros solo añadimos los handlers de dominio encima.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ── Dominio ──────────────────────────────────────────────────────────────

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

    // ── JSON mal formado (fecha/hora con formato incorrecto en el body) ───────

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            Class<?> targetType = ife.getTargetType();
            if (targetType != null) {
                if (LocalDate.class.isAssignableFrom(targetType)) {
                    return ResponseEntity.badRequest().body(
                        Map.of("error", "El formato de fecha no es válido. Usa el formato yyyy-MM-dd (ej. 2025-07-20)."));
                }
                if (LocalTime.class.isAssignableFrom(targetType)) {
                    return ResponseEntity.badRequest().body(
                        Map.of("error", "El formato de hora no es válido. Usa el formato HH:mm (ej. 14:30)."));
                }
            }
        }
        return ResponseEntity.badRequest().body(
            Map.of("error", "El cuerpo de la solicitud tiene un formato incorrecto. Revisa los datos e intenta de nuevo."));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
