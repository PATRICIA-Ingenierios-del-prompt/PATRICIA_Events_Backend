package ingprompt.patricia.events.application.port.in;

import ingprompt.patricia.events.application.dto.PresignedUpload;

public interface RequestPictureUploadCase {
    PresignedUpload requestEventPictureUpload(String contentType, Long declaredSizeBytes);
}
