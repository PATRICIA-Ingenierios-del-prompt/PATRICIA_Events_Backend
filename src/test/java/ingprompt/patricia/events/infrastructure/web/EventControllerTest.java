package ingprompt.patricia.events.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingprompt.patricia.events.application.port.in.EventQueryCase;
import ingprompt.patricia.events.application.port.in.ManageEventCase;
import ingprompt.patricia.events.application.port.in.ManageUserEventCase;
import ingprompt.patricia.events.application.port.in.SpecialQueriesFilterCases;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.exception.EventNotFoundException;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.infrastructure.web.dto.request.CreateEventRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ManageEventCase manageEventCase;
    @MockitoBean
    private ManageUserEventCase manageUserEventCase;
    @MockitoBean
    private EventQueryCase eventQueryCase;
    @MockitoBean
    private SpecialQueriesFilterCases filter;
    @MockitoBean
    private ingprompt.patricia.events.application.port.in.EventMapQueryCase mapQueryCase;

    private final UUID userId = UUID.randomUUID();
    private final UUID eventId = UUID.randomUUID();

    private Event sampleEvent() {
        return new Event(eventId, "Hike", "desc", Category.SPORT, 10, userId,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
    }

    private CreateEventRequest createRequest() {
        CreateEventRequest r = new CreateEventRequest();
        r.setName("Hike");
        r.setCategory(Category.SPORT);
        r.setMaxCapacity(10);
        r.setEventDate(LocalDate.now().plusDays(1));
        r.setStartTime(LocalTime.of(10, 0));
        r.setEndTime(LocalTime.of(12, 0));
        return r;
    }

    @Test
    void createEvent_withHeader_returns200() throws Exception {
        when(manageEventCase.createEvent(any(), any(), any(), any(int.class), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleEvent());

        mockMvc.perform(post("/api/events")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hike"));
    }

    @Test
    void createEvent_withoutHeader_returns400() throws Exception {
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_withLocations_mapsDtosToDomain() throws Exception {
        when(manageEventCase.createEvent(any(), any(), any(), any(int.class), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleEvent());

        CreateEventRequest request = createRequest();
        request.setMeetingPoint(new ingprompt.patricia.events.infrastructure.web.dto.LocationDto(4.6, -74.0, "Meeting", "m-1"));
        request.setDestination(new ingprompt.patricia.events.infrastructure.web.dto.LocationDto(4.7, -74.1, "Dest", "d-1"));

        mockMvc.perform(post("/api/events")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(manageEventCase).createEvent(any(), any(), any(), any(int.class), any(), any(), any(), any(),
                any(ingprompt.patricia.events.domain.model.Location.class),
                any(ingprompt.patricia.events.domain.model.Location.class), any());
    }

    @Test
    void myJoinedEvents_returns200() throws Exception {
        when(mapQueryCase.myJoinedEvents(eq(userId), any())).thenReturn(new PageImpl<>(List.of(sampleEvent())));

        mockMvc.perform(get("/api/events/me").header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].eventId").value(eventId.toString()));
    }

    @Test
    void createEventLinked_returns200() throws Exception {
        when(manageEventCase.createEventLinkedToParche(any(), any(), any(), any(int.class), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleEvent());

        mockMvc.perform(post("/api/events/linked")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEvent_returns204() throws Exception {
        mockMvc.perform(delete("/api/events/{id}", eventId).header("X-User-Id", userId))
                .andExpect(status().isNoContent());
        verify(manageEventCase).deleteEvent(eventId, userId);
    }

    @Test
    void joinEvent_returns204() throws Exception {
        mockMvc.perform(post("/api/events/{id}/join", eventId).header("X-User-Id", userId))
                .andExpect(status().isNoContent());
        verify(manageUserEventCase).joinEvent(userId, eventId);
    }

    @Test
    void removeParticipant_returns204() throws Exception {
        UUID participant = UUID.randomUUID();
        mockMvc.perform(delete("/api/events/{id}/participants/{userId}", eventId, participant).header("X-User-Id", userId))
                .andExpect(status().isNoContent());
        verify(manageUserEventCase).removeUserFromEvent(participant, eventId, userId);
    }

    @Test
    void getEvent_returns200() throws Exception {
        when(eventQueryCase.getEventById(eventId)).thenReturn(sampleEvent());

        mockMvc.perform(get("/api/events/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hike"));
    }

    @Test
    void getEvent_whenMissing_returns404() throws Exception {
        when(eventQueryCase.getEventById(eventId)).thenThrow(new EventNotFoundException(eventId));

        mockMvc.perform(get("/api/events/{id}", eventId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void filterByCategory_returns200() throws Exception {
        when(filter.filterByCategory(eq(Category.SPORT), any())).thenReturn(new PageImpl<>(List.of(sampleEvent())));

        mockMvc.perform(get("/api/events/category").param("category", "SPORT").header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].eventId").value(eventId.toString()));
    }

    @Test
    void filterByCategory_withoutHeader_returns400() throws Exception {
        mockMvc.perform(get("/api/events/category").param("category", "SPORT"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void filterByName_returns200() throws Exception {
        when(filter.findByName(eq("hike"), any())).thenReturn(new PageImpl<>(List.of(sampleEvent())));

        mockMvc.perform(get("/api/events/name").param("name", "hike").header("X-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void filterByDate_returns200() throws Exception {
        when(filter.filterByDate(any(), any())).thenReturn(new PageImpl<>(List.of(sampleEvent())));

        mockMvc.perform(get("/api/events/date").param("date", "2026-07-01").header("X-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void publicMap_returns200WithSpotsLeft() throws Exception {
        when(mapQueryCase.publicOpenEvents(any())).thenReturn(new PageImpl<>(List.of(sampleEvent())));

        mockMvc.perform(get("/api/events/map").header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].eventId").value(eventId.toString()))
                .andExpect(jsonPath("$.content[0].spotsLeft").value(9))   // 10 cap - owner
                .andExpect(jsonPath("$.content[0].maxCapacity").value(10));
    }

    @Test
    void publicMap_withoutHeader_returns400() throws Exception {
        mockMvc.perform(get("/api/events/map"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void myParchesEvents_returns200() throws Exception {
        when(mapQueryCase.myParcheOpenEvents(eq(userId), any())).thenReturn(new PageImpl<>(List.of(sampleEvent())));

        mockMvc.perform(get("/api/events/me/parches/events").header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].eventId").value(eventId.toString()));
    }
}
