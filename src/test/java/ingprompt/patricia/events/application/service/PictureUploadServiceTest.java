package ingprompt.patricia.events.application.service;

import ingprompt.patricia.events.application.dto.PresignedUpload;
import ingprompt.patricia.events.application.port.out.PictureStorageOutPort;
import ingprompt.patricia.events.domain.exception.InvalidPictureUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PictureUploadServiceTest {

    private static final long MAX_BYTES = 5_000_000L;

    @Mock
    private PictureStorageOutPort pictureStorage;

    private PictureUploadService service;

    @BeforeEach
    void setUp() {
        service = new PictureUploadService(pictureStorage, MAX_BYTES);
    }

    @Test
    void requestUpload_withAllowedType_delegates() {
        PresignedUpload expected = new PresignedUpload("https://s3", Map.of("k", "v"), "https://public", "obj");
        when(pictureStorage.generateImageUpload(eq("image/png"), anyLong())).thenReturn(expected);

        assertThat(service.requestEventPictureUpload("image/png", 1024L)).isSameAs(expected);
    }

    @Test
    void requestUpload_normalizesCasing() {
        when(pictureStorage.generateImageUpload(eq("image/jpeg"), anyLong()))
                .thenReturn(new PresignedUpload("u", Map.of(), "p", "o"));

        service.requestEventPictureUpload("IMAGE/JPEG", null);

        verify(pictureStorage).generateImageUpload(eq("image/jpeg"), anyLong());
    }

    @Test
    void requestUpload_unsupportedType_throws() {
        assertThatThrownBy(() -> service.requestEventPictureUpload("image/svg+xml", 1024L))
                .isInstanceOf(InvalidPictureUploadException.class);
    }

    @Test
    void requestUpload_nullType_throws() {
        assertThatThrownBy(() -> service.requestEventPictureUpload(null, 1024L))
                .isInstanceOf(InvalidPictureUploadException.class);
    }

    @Test
    void requestUpload_oversized_throws() {
        assertThatThrownBy(() -> service.requestEventPictureUpload("image/png", MAX_BYTES + 1))
                .isInstanceOf(InvalidPictureUploadException.class);
    }

    @Test
    void requestUpload_zeroSize_throws() {
        assertThatThrownBy(() -> service.requestEventPictureUpload("image/png", 0L))
                .isInstanceOf(InvalidPictureUploadException.class);
    }
}
