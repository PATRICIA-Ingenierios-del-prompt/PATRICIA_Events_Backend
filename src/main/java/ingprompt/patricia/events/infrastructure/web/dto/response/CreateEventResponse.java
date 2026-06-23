package ingprompt.patricia.events.infrastructure.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import ingprompt.patricia.events.domain.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private String pictureUrl;
}
