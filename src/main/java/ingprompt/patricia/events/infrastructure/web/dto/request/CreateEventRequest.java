package ingprompt.patricia.events.infrastructure.web.dto.request;

import ingprompt.patricia.events.domain.enums.Category;
import lombok.Data;

@Data
public class CreateEventRequest {
    private String name;
    private String description;
    private Category category;
    private int maxCapacity;
}
