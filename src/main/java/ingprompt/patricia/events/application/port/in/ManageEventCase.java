package ingprompt.patricia.events.application.port.in;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;

import java.util.UUID;

public interface ManageEventCase {
    Event createEvent(String name, String description, Category category, int maxCapacity, UUID ownerId);
    Event createEventLinkedToParche(String name, String description, Category category, int maxCapacity, UUID parcheId, UUID ownerId);

    void deleteEvent(UUID eventId, UUID ownerId);
}
