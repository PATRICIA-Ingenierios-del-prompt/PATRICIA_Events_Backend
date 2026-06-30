package ingprompt.patricia.events.infrastructure.web;

import ingprompt.patricia.events.application.port.in.EventMapQueryCase;
import ingprompt.patricia.events.application.port.in.EventQueryCase;
import ingprompt.patricia.events.application.port.in.ManageEventCase;
import ingprompt.patricia.events.application.port.in.ManageUserEventCase;
import ingprompt.patricia.events.application.port.in.SpecialQueriesFilterCases;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.domain.model.Location;
import ingprompt.patricia.events.infrastructure.web.dto.LocationDto;
import ingprompt.patricia.events.infrastructure.web.dto.request.CreateEventLinkedToParcheRequest;
import ingprompt.patricia.events.infrastructure.web.dto.request.CreateEventRequest;
import ingprompt.patricia.events.infrastructure.web.dto.response.CreateEventResponse;
import ingprompt.patricia.events.infrastructure.web.dto.response.EventMapResponse;
import ingprompt.patricia.events.infrastructure.web.dto.response.EventResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {
    private final ManageEventCase manageEventCase;
    private final ManageUserEventCase manageUserEventCase;
    private final EventQueryCase eventQueryCase;
    private final SpecialQueriesFilterCases serviceFilter;
    private final EventMapQueryCase mapQueryCase;

    @PostMapping
    public ResponseEntity<CreateEventResponse> createEvent(@RequestBody CreateEventRequest request, @RequestHeader("X-User-Id") UUID ownerId) {
        Event newEvent = manageEventCase.createEvent(request.getName(), request.getDescription(), request.getCategory(), request.getMaxCapacity(), ownerId, request.getEventDate(), request.getStartTime(), request.getEndTime(), toDomain(request.getMeetingPoint()), toDomain(request.getDestination()), request.getPictureUrl());
        return ResponseEntity.ok(toCreateResponse(newEvent));
    }

    @PostMapping("/linked")
    public ResponseEntity<CreateEventResponse> createEventLinkedToParche(@RequestBody CreateEventLinkedToParcheRequest request, @RequestHeader("X-User-Id") UUID ownerId) {
        Event newEvent = manageEventCase.createEventLinkedToParche(request.getName(), request.getDescription(), request.getCategory(), request.getMaxCapacity(), request.getParcheId(), ownerId, request.getEventDate(), request.getStartTime(), request.getEndTime(), toDomain(request.getMeetingPoint()), toDomain(request.getDestination()), request.getPictureUrl());
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

    @GetMapping("/map")
    public ResponseEntity<Page<EventMapResponse>> publicMap(@RequestHeader("X-User-Id") UUID userId, Pageable pageable) {
        return ResponseEntity.ok(mapQueryCase.publicOpenEvents(pageable).map(EventMapResponse::from));
    }

    @GetMapping("/me/parches/events")
    public ResponseEntity<Page<EventMapResponse>> myParchesEvents(@RequestHeader("X-User-Id") UUID userId, Pageable pageable) {
        return ResponseEntity.ok(mapQueryCase.myParcheOpenEvents(userId, pageable).map(EventMapResponse::from));
    }

    @GetMapping("/category")
    public ResponseEntity<Page<EventMapResponse>> filterByCategory(@RequestParam Category category, @RequestHeader("X-User-Id") UUID userId, Pageable pageable) {
        return ResponseEntity.ok(serviceFilter.filterByCategory(category, pageable).map(EventMapResponse::from));
    }

    @GetMapping("/name")
    public ResponseEntity<Page<EventMapResponse>> filterByName(@RequestParam String name, @RequestHeader("X-User-Id") UUID userId, Pageable pageable) {
        return ResponseEntity.ok(serviceFilter.findByName(name, pageable).map(EventMapResponse::from));
    }

    @GetMapping("/date")
    public ResponseEntity<Page<EventMapResponse>> filterByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestHeader("X-User-Id") UUID userId, Pageable pageable) {
        return ResponseEntity.ok(serviceFilter.filterByDate(date, pageable).map(EventMapResponse::from));
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
                event.getEndTime(),
                event.getPictureUrl()
        );
    }
}
