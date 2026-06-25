package ingprompt.patricia.events.application.port.in;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SpecialQueriesFilterCases {
    Page<Event> filterByCategory(Category category, Pageable pageable);
    Page<Event> findByName(String name, Pageable pageable);
    Page<Event> filterByOpenSlots(Pageable pageable);
    Page<Event> filterByDate(LocalDate date, Pageable pageable);
}
