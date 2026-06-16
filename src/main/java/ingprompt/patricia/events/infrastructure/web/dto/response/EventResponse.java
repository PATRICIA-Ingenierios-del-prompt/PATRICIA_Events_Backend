package ingprompt.patricia.events.infrastructure.web.dto.response;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private String name;
    private String description;
    private Category category;
    private int maxCapacity;
    private int participantCount;

    private UUID parcheId;

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getName(),
                event.getDescription(),
                event.getCategory(),
                event.getMaxCapacity(),
                event.getUsersInscribed().size(),
                event.getParcheId()
        );
    }
}
