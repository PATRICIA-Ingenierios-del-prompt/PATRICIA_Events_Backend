package ingprompt.patricia.events.application.dto;

import java.util.Map;

/**
 * Everything the browser needs to upload one image directly to S3 via a presigned POST.
 *
 * @param uploadUrl  the S3 (or S3-compatible) endpoint to POST the multipart form to
 * @param fields     form fields that MUST be submitted before the file part (the file
 *                   field, named "file", goes last). They encode the signed policy.
 * @param publicUrl  the URL the image will be reachable at once uploaded — store this on the Event
 * @param objectKey  the S3 object key (for reference / future deletion)
 */
public record PresignedUpload(String uploadUrl, Map<String, String> fields, String publicUrl, String objectKey) {
}
