package ingprompt.patricia.events.infrastructure.web;

import ingprompt.patricia.events.domain.exception.CannotRemoveOwnerException;
import ingprompt.patricia.events.domain.exception.EventIsFullException;
import ingprompt.patricia.events.domain.exception.EventNotFoundException;
import ingprompt.patricia.events.domain.exception.InvalidEventLocationException;
import ingprompt.patricia.events.domain.exception.InvalidEventScheduleException;
import ingprompt.patricia.events.domain.exception.InvalidPictureUploadException;
import ingprompt.patricia.events.domain.exception.NotEventOwnerException;
import ingprompt.patricia.events.domain.exception.NotEventParticipantException;
import ingprompt.patricia.events.domain.exception.NotParcheMemberException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private final UUID eventId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void handleNotFound_returns404WithMessage() {
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(new EventNotFoundException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("error");
    }

    @Test
    void handleForbidden_returns403() {
        assertThat(handler.handleForbidden(new NotEventOwnerException()).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(handler.handleForbidden(new NotParcheMemberException()).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(handler.handleForbidden(new NotEventParticipantException()).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void handleConflict_returns409() {
        assertThat(handler.handleConflict(new EventIsFullException()).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(handler.handleConflict(new CannotRemoveOwnerException()).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleBadRequest_returns400() {
        assertThat(handler.handleBadRequest(new InvalidEventScheduleException("bad schedule")).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(handler.handleBadRequest(new InvalidEventLocationException("bad location")).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<Map<String, String>> response =
                handler.handleBadRequest(new InvalidPictureUploadException("bad picture"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "bad picture");
    }
}
