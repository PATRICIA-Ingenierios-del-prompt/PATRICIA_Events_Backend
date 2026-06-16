package ingprompt.patricia.events.infrastructure.web.dto.response;

import ingprompt.patricia.events.domain.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventResponse {
    private UUID eventId;
    private String name;
    private String description;
    private Category category;

    private UUID parcheId;
}
