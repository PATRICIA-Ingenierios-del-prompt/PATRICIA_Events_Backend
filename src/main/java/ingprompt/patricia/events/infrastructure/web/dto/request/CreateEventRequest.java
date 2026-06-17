package ingprompt.patricia.events.infrastructure.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.infrastructure.web.dto.LocationDto;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateEventRequest {
    private String name;
    private String description;
    private Category category;
    private int maxCapacity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;


    private LocationDto meetingPoint;
    private LocationDto destination;
}
