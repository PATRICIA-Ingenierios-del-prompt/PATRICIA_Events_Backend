-- Event picture: we store only the S3/CDN URL, never the image bytes.
ALTER TABLE events
    ADD COLUMN picture_url VARCHAR(1000);
