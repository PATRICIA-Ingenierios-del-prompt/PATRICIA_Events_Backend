package ingprompt.patricia.events.infrastructure.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.infrastructure.web.dto.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Projection for the map view: just enough to render an event as a clickable
 * point. Only ever built for events that still have open slots.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMapResponse {
    private UUID eventId;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    private LocationDto destination;
    private Category category;
    private String description;
    private int spotsLeft;
    private int maxCapacity;

    public static EventMapResponse from(Event event) {
        return new EventMapResponse(
                event.getEventId(),
                event.getName(),
                event.getEventDate(),
                event.getStartTime(),
                LocationDto.from(event.getDestination()),
                event.getCategory(),
                event.getDescription(),
                event.getMaxCapacity() - event.getUsersInscribed().size(),
                event.getMaxCapacity()
        );
    }
}
