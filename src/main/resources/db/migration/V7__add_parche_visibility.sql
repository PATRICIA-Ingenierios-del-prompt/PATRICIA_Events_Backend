-- Local read model of parche visibility, fed by parche.created events from the
-- Parches MS. Lets the Events MS tell apart events of PUBLIC parches (shown on the
-- public map) from PRIVATE ones (members only).
CREATE TABLE parche_visibility (
    parche_id  UUID         PRIMARY KEY,
    visibility VARCHAR(50)  NOT NULL
);

CREATE INDEX idx_parche_visibility_visibility ON parche_visibility (visibility);
