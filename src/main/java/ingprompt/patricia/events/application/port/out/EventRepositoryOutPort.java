package ingprompt.patricia.events.application.port.out;

import ingprompt.patricia.events.domain.model.Event;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepositoryOutPort {
    void save(Event event);
    void delete(Event event);
    Optional<Event> findById(UUID eventId);

    void deleteAllByIds(Collection<UUID> eventIds);

    /** Candidates for "starting": not yet started and dated on/before the given day. */
    List<Event> findStartableCandidates(LocalDate onOrBefore);
}
