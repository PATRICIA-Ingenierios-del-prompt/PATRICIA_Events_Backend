package ingprompt.patricia.events.application.port.in;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.domain.model.Location;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface ManageEventCase {
    Event createEvent(String name, String description, Category category, int maxCapacity, UUID ownerId, LocalDate eventDate, LocalTime startTime, LocalTime endTime, Location meetingPoint, Location destination, String pictureUrl);
    Event createEventLinkedToParche(String name, String description, Category category, int maxCapacity, UUID parcheId, UUID ownerId, LocalDate eventDate, LocalTime startTime, LocalTime endTime, Location meetingPoint, Location destination, String pictureUrl);
    void deleteEvent(UUID eventId, UUID ownerId);
}
