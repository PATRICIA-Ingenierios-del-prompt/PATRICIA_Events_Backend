package ingprompt.patricia.events.infrastructure.web;

import ingprompt.patricia.events.application.dto.PresignedUpload;
import ingprompt.patricia.events.application.port.in.RequestPictureUploadCase;
import ingprompt.patricia.events.infrastructure.web.dto.request.PictureUploadRequest;
import ingprompt.patricia.events.infrastructure.web.dto.response.PictureUploadResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events/picture-upload-url")
@AllArgsConstructor
public class PictureUploadController {
    private final RequestPictureUploadCase requestPictureUploadCase;

    @PostMapping
    public ResponseEntity<PictureUploadResponse> requestUpload(@Valid @RequestBody PictureUploadRequest request,
                                                              @RequestHeader("X-User-Id") UUID requesterId) {
        PresignedUpload upload = requestPictureUploadCase
                .requestEventPictureUpload(request.getContentType(), request.getFileSize());
        return ResponseEntity.ok(PictureUploadResponse.from(upload));
    }
}
