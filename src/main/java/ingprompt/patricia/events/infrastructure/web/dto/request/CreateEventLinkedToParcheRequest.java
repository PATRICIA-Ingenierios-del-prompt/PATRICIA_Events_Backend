package ingprompt.patricia.events.infrastructure.web.dto.request;

import ingprompt.patricia.events.domain.enums.Category;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateEventLinkedToParcheRequest {
    private String name;
    private String description;
    private Category category;
    private int maxCapacity;
    private UUID parcheId;
}
