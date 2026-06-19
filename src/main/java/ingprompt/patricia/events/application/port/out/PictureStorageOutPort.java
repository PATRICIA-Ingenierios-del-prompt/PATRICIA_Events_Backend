package ingprompt.patricia.events.application.port.out;

import ingprompt.patricia.events.application.dto.PresignedUpload;

public interface PictureStorageOutPort {
    PresignedUpload generateImageUpload(String contentType, long maxBytes);
}
