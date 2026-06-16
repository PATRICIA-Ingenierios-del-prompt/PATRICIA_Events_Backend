package ingprompt.patricia.events.infrastructure.persistence.repository;

import ingprompt.patricia.events.application.port.out.EventRepositoryOutPort;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.infrastructure.persistence.repository.mapper.EventMapper;
import ingprompt.patricia.events.infrastructure.persistence.postgre.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
}
