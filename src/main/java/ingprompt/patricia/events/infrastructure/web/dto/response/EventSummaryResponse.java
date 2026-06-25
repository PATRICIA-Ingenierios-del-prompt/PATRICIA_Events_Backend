package ingprompt.patricia.events.infrastructure.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Lean projection of an Event for discovery/filter result lists.
 * Unlike {@link EventResponse} it carries the eventId so the client
 * can navigate to a result.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSummaryResponse {
    private UUID eventId;
    private String name;
    private String description;
    private Category category;
    private int maxCapacity;
    private int participantCount;
    private boolean started;

    private UUID parcheId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private String pictureUrl;

    public static EventSummaryResponse from(Event event) {
        return new EventSummaryResponse(
                event.getEventId(),
                event.getName(),
                event.getDescription(),
                event.getCategory(),
                event.getMaxCapacity(),
                event.getUsersInscribed().size(),
                event.isStarted(),
                event.getParcheId(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getPictureUrl()
        );
    }
}
