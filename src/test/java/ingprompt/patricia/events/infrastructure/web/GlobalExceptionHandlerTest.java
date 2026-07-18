package ingprompt.patricia.events.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingprompt.patricia.events.application.port.in.EventMapQueryCase;
import ingprompt.patricia.events.application.port.in.EventQueryCase;
import ingprompt.patricia.events.application.port.in.ManageEventCase;
import ingprompt.patricia.events.application.port.in.ManageUserEventCase;
import ingprompt.patricia.events.application.port.in.SpecialQueriesFilterCases;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests para GlobalExceptionHandler.
 *
 * Los handlers de dominio (handleNotFound, handleForbidden, etc.) se prueban
 * directamente sobre la instancia para máxima velocidad y cobertura de línea.
 *
 * Los tests de handleHttpMessageNotReadable usan @WebMvcTest + MockMvc porque
 * ese método solo se invoca a través del ciclo de deserialización de Jackson
 * dentro de Spring MVC — no se puede llamar directamente de forma útil.
 */
@WebMvcTest(EventController.class)
class GlobalExceptionHandlerTest {

    // ── Instancia directa para los handlers de dominio ────────────────────────

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ── MockMvc para los tests de deserialización JSON ────────────────────────

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private ManageEventCase manageEventCase;
    @MockitoBean private ManageUserEventCase manageUserEventCase;
    @MockitoBean private EventQueryCase eventQueryCase;
    @MockitoBean private SpecialQueriesFilterCases filter;
    @MockitoBean private EventMapQueryCase mapQueryCase;

    private static final UUID USER_ID = UUID.randomUUID();

    // ── Handlers de dominio ───────────────────────────────────────────────────

    @Test
    void handleNotFound_returns404WithErrorKey() {
        ResponseEntity<Map<String, String>> response =
                handler.handleNotFound(new EventNotFoundException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("error");
        assertThat(response.getBody().get("error")).isNotBlank();
    }

    @Test
    void handleForbidden_returns403_forAllForbiddenExceptions() {
        assertThat(handler.handleForbidden(new NotEventOwnerException()).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(handler.handleForbidden(new NotParcheMemberException()).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(handler.handleForbidden(new NotEventParticipantException()).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void handleConflict_returns409_forAllConflictExceptions() {
        assertThat(handler.handleConflict(new EventIsFullException()).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(handler.handleConflict(new CannotRemoveOwnerException()).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleBadRequest_returns400_andPreservesMessage() {
        assertThat(handler.handleBadRequest(new InvalidEventScheduleException("bad schedule"))
                .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(handler.handleBadRequest(new InvalidEventLocationException("bad location"))
                .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<Map<String, String>> response =
                handler.handleBadRequest(new InvalidPictureUploadException("bad picture"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "bad picture");
    }

    // ── handleHttpMessageNotReadable — todas las ramas ────────────────────────

    @Test
    void createEvent_withInvalidDateFormat_returns400WithDateMessage() throws Exception {
        String body = """
                {
                  "name": "Test",
                  "eventDate": "not-a-date",
                  "startTime": "10:00",
                  "endTime": "12:00",
                  "maxCapacity": 10,
                  "category": "SPORT"
                }
                """;

        mockMvc.perform(post("/api/events")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("fecha")));
    }

    @Test
    void createEvent_withInvalidTimeFormat_returns400WithTimeMessage() throws Exception {
        String body = """
                {
                  "name": "Test",
                  "eventDate": "2026-08-01",
                  "startTime": "not-a-time",
                  "endTime": "12:00",
                  "maxCapacity": 10,
                  "category": "SPORT"
                }
                """;

        mockMvc.perform(post("/api/events")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("hora")));
    }

    @Test
    void createEvent_withMalformedJson_returns400WithGenericMessage() throws Exception {
        String body = "{ this is not valid json at all }";

        mockMvc.perform(post("/api/events")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("formato")));
    }

    @Test
    void filterByDate_withInvalidDateParam_returns400() throws Exception {
        mockMvc.perform(get("/api/events/date")
                        .header("X-User-Id", USER_ID)
                        .param("date", "not-a-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEvent_whenNotFound_returns404WithErrorKey() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(eventQueryCase.getEventById(any())).thenThrow(new EventNotFoundException());

        mockMvc.perform(get("/api/events/{id}", eventId)
                        .header("X-User-Id", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
