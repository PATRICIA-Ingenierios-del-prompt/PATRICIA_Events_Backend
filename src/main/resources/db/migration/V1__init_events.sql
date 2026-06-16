-- Events table. parche_id is nullable: standalone events have it as NULL.
CREATE TABLE events (
    event_id     UUID         PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(1000),
    owner_id     UUID         NOT NULL,
    category     VARCHAR(50)  NOT NULL,
    max_capacity INTEGER      NOT NULL,
    parche_id    UUID
);

CREATE INDEX idx_events_owner_id  ON events (owner_id);
CREATE INDEX idx_events_parche_id ON events (parche_id);
CREATE INDEX idx_events_category  ON events (category);

-- @ElementCollection: event_id + user_id, both part of the PK.
CREATE TABLE event_participants (
    event_id UUID NOT NULL REFERENCES events (event_id) ON DELETE CASCADE,
    user_id  UUID NOT NULL,
    PRIMARY KEY (event_id, user_id)
);

CREATE INDEX idx_event_participants_user_id ON event_participants (user_id);
