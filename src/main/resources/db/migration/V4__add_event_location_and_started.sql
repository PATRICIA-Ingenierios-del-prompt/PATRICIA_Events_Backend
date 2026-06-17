-- Lifecycle flag for the event.started scheduler.
ALTER TABLE events
    ADD COLUMN started BOOLEAN NOT NULL DEFAULT FALSE;

-- Destination (required) — where the event takes place.
-- NOT NULL assumes no pre-existing rows; run `docker compose down -v` if you have local data.
ALTER TABLE events
    ADD COLUMN destination_latitude  DOUBLE PRECISION NOT NULL,
    ADD COLUMN destination_longitude DOUBLE PRECISION NOT NULL,
    ADD COLUMN destination_address   VARCHAR(500),
    ADD COLUMN destination_place_id  VARCHAR(255);

-- Meeting point (optional) — rendezvous before heading to the destination.
ALTER TABLE events
    ADD COLUMN meeting_latitude  DOUBLE PRECISION,
    ADD COLUMN meeting_longitude DOUBLE PRECISION,
    ADD COLUMN meeting_address   VARCHAR(500),
    ADD COLUMN meeting_place_id  VARCHAR(255);

-- The scheduler scans by date for not-yet-started events.
CREATE INDEX idx_events_started_date ON events (started, event_date);
