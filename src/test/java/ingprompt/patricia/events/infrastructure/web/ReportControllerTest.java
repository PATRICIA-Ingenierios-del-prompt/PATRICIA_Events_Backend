package ingprompt.patricia.events.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingprompt.patricia.events.application.port.in.ManageReportCase;
import ingprompt.patricia.events.domain.enums.ReportType;
import ingprompt.patricia.events.domain.exception.NotEventParticipantException;
import ingprompt.patricia.events.domain.model.Report;
import ingprompt.patricia.events.infrastructure.web.dto.request.CreateReportRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ManageReportCase manageReportCase;

    private final UUID userId = UUID.randomUUID();
    private final UUID eventId = UUID.randomUUID();

    private CreateReportRequest request() {
        CreateReportRequest r = new CreateReportRequest();
        r.setReportType(ReportType.ACCIDENT);
        r.setDescription("fell");
        return r;
    }

    @Test
    void createReport_withHeader_returns200() throws Exception {
        Report report = Report.create(eventId, userId, ReportType.ACCIDENT, "fell");
        when(manageReportCase.createReport(eq(eventId), eq(userId), eq(ReportType.ACCIDENT), any()))
                .thenReturn(report);

        mockMvc.perform(post("/api/events/{eventId}/reports", eventId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(eventId.toString()))
                .andExpect(jsonPath("$.reportType").value("ACCIDENT"));
    }

    @Test
    void createReport_withoutHeader_returns400() throws Exception {
        mockMvc.perform(post("/api/events/{eventId}/reports", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReport_byNonParticipant_returns403() throws Exception {
        when(manageReportCase.createReport(any(), any(), any(), any()))
                .thenThrow(new NotEventParticipantException(userId, eventId));

        mockMvc.perform(post("/api/events/{eventId}/reports", eventId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").exists());
    }
}
