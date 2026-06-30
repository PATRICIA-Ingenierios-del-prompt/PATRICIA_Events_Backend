package ingprompt.patricia.events.application.port.out;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    List<Event> findStartableCandidates(LocalDate onOrBefore);
    List<Event> findFinishableCandidates(LocalDate onOrBefore);

    Page<Event> findByCategory(Category category, Pageable pageable);
    Page<Event> findByNameContaining(String name, Pageable pageable);
    Page<Event> findByEventDate(LocalDate date, Pageable pageable);

    Page<Event> findPublicOpenEvents(Pageable pageable);
    Page<Event> findOpenEventsForParches(Collection<UUID> parcheIds, Pageable pageable);
}
