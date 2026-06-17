-- Local read model of which user belongs to which parche.
-- Fed by inbound RabbitMQ events from the Parches MS.
CREATE TABLE parche_memberships (
    parche_id UUID NOT NULL,
    user_id   UUID NOT NULL,
    PRIMARY KEY (parche_id, user_id)
);

-- "Which parches does this user belong to?" — for the UI's collapsible list.
CREATE INDEX idx_parche_memberships_user_id ON parche_memberships (user_id);