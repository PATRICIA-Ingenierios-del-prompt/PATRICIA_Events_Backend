package ingprompt.patricia.events.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingprompt.patricia.events.application.dto.PresignedUpload;
import ingprompt.patricia.events.application.port.in.RequestPictureUploadCase;
import ingprompt.patricia.events.domain.exception.InvalidPictureUploadException;
import ingprompt.patricia.events.infrastructure.web.dto.request.PictureUploadRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PictureUploadController.class)
class PictureUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RequestPictureUploadCase requestPictureUploadCase;

    private final UUID userId = UUID.randomUUID();

    private PictureUploadRequest request(String contentType) {
        PictureUploadRequest r = new PictureUploadRequest();
        r.setContentType(contentType);
        r.setFileSize(1024L);
        return r;
    }

    @Test
    void requestUpload_valid_returns200() throws Exception {
        when(requestPictureUploadCase.requestEventPictureUpload(any(), any()))
                .thenReturn(new PresignedUpload("https://s3", Map.of("k", "v"), "https://public", "obj"));

        mockMvc.perform(post("/api/events/picture-upload-url")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("image/png"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectKey").value("obj"));
    }

    @Test
    void requestUpload_blankContentType_returns400() throws Exception {
        mockMvc.perform(post("/api/events/picture-upload-url")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("  "))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestUpload_whenServiceRejects_returns400() throws Exception {
        when(requestPictureUploadCase.requestEventPictureUpload(any(), any()))
                .thenThrow(new InvalidPictureUploadException("bad"));

        mockMvc.perform(post("/api/events/picture-upload-url")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("image/svg+xml"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestUpload_withoutHeader_returns400() throws Exception {
        mockMvc.perform(post("/api/events/picture-upload-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("image/png"))))
                .andExpect(status().isBadRequest());
    }
}
