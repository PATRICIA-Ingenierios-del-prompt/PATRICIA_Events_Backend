package ingprompt.patricia.events.infrastructure.storage;

import ingprompt.patricia.events.application.dto.PresignedUpload;
import ingprompt.patricia.events.domain.exception.InvalidPictureUploadException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class S3PresignedPostAdapterTest {

    private static final long MAX_BYTES = 5_000_000L;

    private S3PresignedPostAdapter adapter(String accessKey, String secret, String sessionToken,
                                           String endpoint, String publicBaseUrl) {
        return new S3PresignedPostAdapter(accessKey, secret, sessionToken,
                "us-east-1", "patricia-events", endpoint, publicBaseUrl, 300L);
    }

    @Test
    void generateImageUpload_buildsSignedPostForRealS3() {
        S3PresignedPostAdapter adapter = adapter("AKIATEST", "secret", "", "", "");

        PresignedUpload upload = adapter.generateImageUpload("image/png", MAX_BYTES);

        assertThat(upload.uploadUrl()).isEqualTo("https://patricia-events.s3.us-east-1.amazonaws.com");
        assertThat(upload.objectKey()).startsWith("events/pictures/").endsWith(".png");
        assertThat(upload.publicUrl()).isEqualTo(upload.uploadUrl() + "/" + upload.objectKey());
        assertThat(upload.fields())
                .containsKeys("key", "acl", "Content-Type", "x-amz-algorithm",
                        "x-amz-credential", "x-amz-date", "policy", "x-amz-signature");
        assertThat(upload.fields().get("Content-Type")).isEqualTo("image/png");
        assertThat(upload.fields()).doesNotContainKey("x-amz-security-token");
    }

    @Test
    void generateImageUpload_usesPathStyleAndPublicBaseUrl() {
        S3PresignedPostAdapter adapter = adapter("AKIATEST", "secret", "",
                "http://localhost:4566/", "https://cdn.patricia.app/");

        PresignedUpload upload = adapter.generateImageUpload("image/jpeg", MAX_BYTES);

        assertThat(upload.uploadUrl()).isEqualTo("http://localhost:4566/patricia-events");
        assertThat(upload.objectKey()).endsWith(".jpg");
        assertThat(upload.publicUrl()).startsWith("https://cdn.patricia.app/events/pictures/");
    }

    @Test
    void generateImageUpload_includesSecurityTokenWhenPresent() {
        S3PresignedPostAdapter adapter = adapter("AKIATEST", "secret", "SESSIONTOKEN", "", "");

        PresignedUpload upload = adapter.generateImageUpload("image/gif", MAX_BYTES);

        assertThat(upload.fields()).containsEntry("x-amz-security-token", "SESSIONTOKEN");
        assertThat(upload.objectKey()).endsWith(".gif");
    }

    @Test
    void generateImageUpload_webpMapsToWebpExtension() {
        S3PresignedPostAdapter adapter = adapter("AKIATEST", "secret", "", "", "");

        PresignedUpload upload = adapter.generateImageUpload("image/webp", MAX_BYTES);

        assertThat(upload.objectKey()).endsWith(".webp");
    }

    @Test
    void generateImageUpload_unknownContentTypeFallsBackToBin() {
        S3PresignedPostAdapter adapter = adapter("AKIATEST", "secret", "", "", "");

        PresignedUpload upload = adapter.generateImageUpload("application/octet-stream", MAX_BYTES);

        assertThat(upload.objectKey()).endsWith(".bin");
    }

    @Test
    void generateImageUpload_endpointWithoutTrailingSlash_isUsedAsIs() {
        S3PresignedPostAdapter adapter = adapter("AKIATEST", "secret", "",
                "http://localhost:4566", "https://cdn.patricia.app");

        PresignedUpload upload = adapter.generateImageUpload("image/png", MAX_BYTES);

        assertThat(upload.uploadUrl()).isEqualTo("http://localhost:4566/patricia-events");
        assertThat(upload.publicUrl()).startsWith("https://cdn.patricia.app/events/pictures/");
    }

    @Test
    void generateImageUpload_withoutCredentials_throws() {
        S3PresignedPostAdapter adapter = adapter("", "", "", "", "");

        assertThatThrownBy(() -> adapter.generateImageUpload("image/png", MAX_BYTES))
                .isInstanceOf(InvalidPictureUploadException.class);
    }
}
