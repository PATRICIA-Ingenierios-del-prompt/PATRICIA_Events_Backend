-- Lifecycle flag for the event.ended scheduler.
ALTER TABLE events
    ADD COLUMN finished BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_events_finished_date ON events (finished, event_date);

-- Incident reports: their own table, referencing the event by id.
CREATE TABLE reports (
    report_id   UUID         PRIMARY KEY,
    event_id    UUID         NOT NULL,
    reporter_id UUID         NOT NULL,
    reported_at TIMESTAMPTZ  NOT NULL,
    report_type VARCHAR(40)  NOT NULL,
    description VARCHAR(2000)
);

CREATE INDEX idx_reports_event_id    ON reports (event_id);
CREATE INDEX idx_reports_reporter_id ON reports (reporter_id);
CREATE INDEX idx_reports_type        ON reports (report_type);
