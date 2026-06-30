package ingprompt.patricia.events.infrastructure.persistence.repository;

import ingprompt.patricia.events.application.port.out.EventRepositoryOutPort;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.infrastructure.persistence.repository.mapper.EventMapper;
import ingprompt.patricia.events.infrastructure.persistence.postgre.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class EventRepositoryAdapter implements EventRepositoryOutPort {
    private final EventRepository postgreRepository;

    @Override
    public void save(Event event) {
        postgreRepository.save(EventMapper.toEntity(event));
    }

    @Override
    public void delete(Event event) {
        postgreRepository.deleteById(event.getEventId());
    }

    @Override
    public Optional<Event> findById(UUID eventId) {
        return postgreRepository.findById(eventId).map(EventMapper::toDomain);
    }

    @Override
    public void deleteAllByIds(Collection<UUID> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return;
        }
        postgreRepository.deleteAllByIdInBatch(eventIds);
    }

    @Override
    public List<Event> findStartableCandidates(LocalDate onOrBefore) {
        return postgreRepository.findByStartedFalseAndEventDateLessThanEqual(onOrBefore)
                .stream()
                .map(EventMapper::toDomain)
                .toList();
    }

    @Override
    public List<Event> findFinishableCandidates(LocalDate onOrBefore) {
        return postgreRepository.findByFinishedFalseAndEventDateLessThanEqual(onOrBefore)
                .stream()
                .map(EventMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Event> findByCategory(Category category, Pageable pageable) {
        return postgreRepository.findVisibleOpenByCategory(category, pageable).map(EventMapper::toDomain);
    }

    @Override
    public Page<Event> findByNameContaining(String name, Pageable pageable) {
        return postgreRepository.findVisibleOpenByName(name, pageable).map(EventMapper::toDomain);
    }

    @Override
    public Page<Event> findByEventDate(LocalDate date, Pageable pageable) {
        return postgreRepository.findVisibleOpenByDate(date, pageable).map(EventMapper::toDomain);
    }

    @Override
    public Page<Event> findPublicOpenEvents(Pageable pageable) {
        return postgreRepository.findPublicOpenEvents(pageable).map(EventMapper::toDomain);
    }

    @Override
    public Page<Event> findOpenEventsForParches(Collection<UUID> parcheIds, Pageable pageable) {
        return postgreRepository.findOpenEventsForParches(parcheIds, pageable).map(EventMapper::toDomain);
    }
}
