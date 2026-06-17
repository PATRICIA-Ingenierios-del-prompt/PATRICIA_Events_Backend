-- Adds the schedule fields (date + start + end time) to events.
-- Existing rows would block NOT NULL; this assumes no production data yet.
-- If you have local data: `docker compose down -v` before bringing the stack back up.
ALTER TABLE events
    ADD COLUMN event_date DATE NOT NULL,
    ADD COLUMN start_time TIME NOT NULL,
    ADD COLUMN end_time   TIME NOT NULL;

CREATE INDEX idx_events_event_date ON events (event_date);
