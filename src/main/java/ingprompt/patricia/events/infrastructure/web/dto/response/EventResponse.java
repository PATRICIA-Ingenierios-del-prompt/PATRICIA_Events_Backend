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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
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

    private LocationDto meetingPoint;
    private LocationDto destination;

    public static EventResponse from(Event event) {
        return new EventResponse(
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
                LocationDto.from(event.getMeetingPoint()),
                LocationDto.from(event.getDestination())
        );
    }
}
