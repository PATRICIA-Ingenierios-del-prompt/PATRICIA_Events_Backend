package ingprompt.patricia.events.application.port.out;

import ingprompt.patricia.events.domain.model.Event;

import java.util.Optional;
import java.util.UUID;

public interface EventRepositoryOutPort {
    void save(Event event);
    void delete(Event event);
    Optional<Event> findById(UUID eventId);
}
