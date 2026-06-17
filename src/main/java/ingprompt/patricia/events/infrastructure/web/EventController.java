package ingprompt.patricia.events.infrastructure.web;

import ingprompt.patricia.events.application.port.in.EventQueryCase;
import ingprompt.patricia.events.application.port.in.ManageEventCase;
import ingprompt.patricia.events.application.port.in.ManageUserEventCase;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.domain.model.Location;
import ingprompt.patricia.events.infrastructure.web.dto.LocationDto;
import ingprompt.patricia.events.infrastructure.web.dto.request.CreateEventLinkedToParcheRequest;
import ingprompt.patricia.events.infrastructure.web.dto.request.CreateEventRequest;
import ingprompt.patricia.events.infrastructure.web.dto.response.CreateEventResponse;
import ingprompt.patricia.events.infrastructure.web.dto.response.EventResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {
    private final ManageEventCase manageEventCase;
    private final ManageUserEventCase manageUserEventCase;
    private final EventQueryCase eventQueryCase;

    @PostMapping
    public ResponseEntity<CreateEventResponse> createEvent(@RequestBody CreateEventRequest request, @RequestHeader("X-User-Id") UUID ownerId) {
        Event newEvent = manageEventCase.createEvent(request.getName(), request.getDescription(), request.getCategory(), request.getMaxCapacity(), ownerId, request.getEventDate(), request.getStartTime(), request.getEndTime(), toDomain(request.getMeetingPoint()), toDomain(request.getDestination()));
        return ResponseEntity.ok(toCreateResponse(newEvent));
    }

    @PostMapping("/linked")
    public ResponseEntity<CreateEventResponse> createEventLinkedToParche(@RequestBody CreateEventLinkedToParcheRequest request, @RequestHeader("X-User-Id") UUID ownerId) {
        Event newEvent = manageEventCase.createEventLinkedToParche(request.getName(), request.getDescription(), request.getCategory(), request.getMaxCapacity(), request.getParcheId(), ownerId, request.getEventDate(), request.getStartTime(), request.getEndTime(), toDomain(request.getMeetingPoint()), toDomain(request.getDestination()));
        return ResponseEntity.ok(toCreateResponse(newEvent));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId, @RequestHeader("X-User-Id") UUID ownerId) {
        manageEventCase.deleteEvent(eventId, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/join")
    public ResponseEntity<Void> joinEvent(@PathVariable UUID eventId, @RequestHeader("X-User-Id") UUID userId) {
        manageUserEventCase.joinEvent(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<Void> removeParticipant(@PathVariable UUID eventId, @PathVariable UUID userId, @RequestHeader("X-User-Id") UUID requesterId) {
        manageUserEventCase.removeUserFromEvent(userId, eventId, requesterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable UUID eventId) {
        return ResponseEntity.ok(EventResponse.from(eventQueryCase.getEventById(eventId)));
    }

    private static Location toDomain(LocationDto dto) {
        return dto == null ? null : dto.toDomain();
    }

    private static CreateEventResponse toCreateResponse(Event event) {
        return new CreateEventResponse(
                event.getEventId(),
                event.getName(),
                event.getDescription(),
                event.getCategory(),
                event.getParcheId(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime()
        );
    }
}
