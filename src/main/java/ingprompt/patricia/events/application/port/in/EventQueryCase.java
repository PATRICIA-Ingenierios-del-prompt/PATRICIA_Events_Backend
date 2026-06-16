package ingprompt.patricia.events.application.port.in;

import ingprompt.patricia.events.domain.model.Event;

import java.util.UUID;

public interface EventQueryCase {
    Event getEventById(UUID eventId);
}
