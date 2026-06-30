package ingprompt.patricia.events.application.port.in;

import ingprompt.patricia.events.domain.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface EventMapQueryCase {
    Page<Event> publicOpenEvents(Pageable pageable);
    Page<Event> myParcheOpenEvents(UUID userId, Pageable pageable);
}
