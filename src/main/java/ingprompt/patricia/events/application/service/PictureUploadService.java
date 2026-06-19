package ingprompt.patricia.events.application.service;

import ingprompt.patricia.events.application.dto.PresignedUpload;
import ingprompt.patricia.events.application.port.in.RequestPictureUploadCase;
import ingprompt.patricia.events.application.port.out.PictureStorageOutPort;
import ingprompt.patricia.events.domain.exception.InvalidPictureUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PictureUploadService implements RequestPictureUploadCase {
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp",
            "image/gif"
    );

    private final PictureStorageOutPort pictureStorage;
    private final long maxBytes;

    public PictureUploadService(PictureStorageOutPort pictureStorage, @Value("${event.picture.max-bytes}") long maxBytes) {
        this.pictureStorage = pictureStorage;
        this.maxBytes = maxBytes;
    }

    @Override
    public PresignedUpload requestEventPictureUpload(String contentType, Long declaredSizeBytes) {
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidPictureUploadException(
                    "Unsupported image type. Allowed: " + ALLOWED_TYPES);
        }
        // Early UX reject; S3's content-length-range still hard-enforces the cap server-side.
        if (declaredSizeBytes != null && (declaredSizeBytes <= 0 || declaredSizeBytes > maxBytes)) {
            throw new InvalidPictureUploadException(
                    "Image must be between 1 byte and " + maxBytes + " bytes");
        }
        return pictureStorage.generateImageUpload(contentType.toLowerCase(), maxBytes);
    }
}
