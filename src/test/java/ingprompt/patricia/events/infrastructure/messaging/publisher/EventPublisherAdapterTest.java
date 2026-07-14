package ingprompt.patricia.events.infrastructure.messaging.publisher;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.infrastructure.messaging.config.RabbitMQConfig;
import ingprompt.patricia.events.infrastructure.messaging.event.EventCreatedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.EventDeletedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.EventEndedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.EventLinkedToParcheEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.EventStartedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.IncidentReportedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.ParticipantJoinedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventPublisherAdapterTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private EventPublisherAdapter adapter;

    private final UUID eventId = UUID.randomUUID();
    private final UUID parcheId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new EventPublisherAdapter(rabbitTemplate);
    }

    @Test
    void publishEventCreated_sendsEnrichedBroadcast() {
        adapter.publishEventCreated(eventId, "Hike", userId, false, Category.SPORT);

        ArgumentCaptor<EventCreatedEvent> body = ArgumentCaptor.forClass(EventCreatedEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EVENT_EXCHANGE), eq(RabbitMQConfig.EVENT_CREATED_ROUTING_KEY), body.capture());
        assertThat(body.getValue().getEventId()).isEqualTo(eventId);
        assertThat(body.getValue().getName()).isEqualTo("Hike");
        assertThat(body.getValue().getOwnerId()).isEqualTo(userId);
        assertThat(body.getValue().isLinkedToParche()).isFalse();
        assertThat(body.getValue().getCategory()).isEqualTo(Category.SPORT);
        assertThat(body.getValue().getSourceEventId()).isNotNull();
    }

    @Test
    void publishEventLinkedToParche_sendsEnrichedEvent() {
        Set<UUID> members = Set.of(UUID.randomUUID(), userId);
        adapter.publishEventLinkedToParche(eventId, "Hike", parcheId, "Cálculo III", userId, members);

        ArgumentCaptor<EventLinkedToParcheEvent> body = ArgumentCaptor.forClass(EventLinkedToParcheEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EVENT_EXCHANGE), eq(RabbitMQConfig.EVENT_LINKED_ROUTING_KEY), body.capture());
        assertThat(body.getValue().getEventId()).isEqualTo(eventId);
        assertThat(body.getValue().getEventName()).isEqualTo("Hike");
        assertThat(body.getValue().getParcheId()).isEqualTo(parcheId);
        assertThat(body.getValue().getParcheName()).isEqualTo("Cálculo III");
        assertThat(body.getValue().getUserId()).isEqualTo(userId);
        assertThat(body.getValue().getMemberIds()).isEqualTo(members);
        assertThat(body.getValue().getSourceEventId()).isNotNull();
    }

    @Test
    void publishEventDeleted_sendsEvent() {
        adapter.publishEventDeleted(eventId, parcheId);

        ArgumentCaptor<EventDeletedEvent> body = ArgumentCaptor.forClass(EventDeletedEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EVENT_EXCHANGE), eq(RabbitMQConfig.EVENT_DELETED_ROUTING_KEY), body.capture());
        assertThat(body.getValue().getEventId()).isEqualTo(eventId);
    }

    @Test
    void publishEventStarted_sendsParticipants() {
        Set<UUID> participants = Set.of(userId);
        adapter.publishEventStarted(eventId, participants);

        ArgumentCaptor<EventStartedEvent> body = ArgumentCaptor.forClass(EventStartedEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EVENT_EXCHANGE), eq(RabbitMQConfig.EVENT_STARTED_ROUTING_KEY), body.capture());
        assertThat(body.getValue().getParticipants()).isEqualTo(participants);
    }

    @Test
    void publishEventEnded_sendsEvent() {
        adapter.publishEventEnded(eventId);

        ArgumentCaptor<EventEndedEvent> body = ArgumentCaptor.forClass(EventEndedEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EVENT_EXCHANGE), eq(RabbitMQConfig.EVENT_ENDED_ROUTING_KEY), body.capture());
        assertThat(body.getValue().getEventId()).isEqualTo(eventId);
    }

    @Test
    void publishIncidentReported_sendsEvent() {
        UUID reportId = UUID.randomUUID();
        adapter.publishIncidentReported(eventId, reportId, userId);

        ArgumentCaptor<IncidentReportedEvent> body = ArgumentCaptor.forClass(IncidentReportedEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EVENT_EXCHANGE), eq(RabbitMQConfig.EVENT_INCIDENT_REPORTED_ROUTING_KEY), body.capture());
        assertThat(body.getValue().getReportId()).isEqualTo(reportId);
    }

    @Test
    void publishParticipantJoined_sendsEvent() {
        adapter.publishParticipantJoined(eventId, userId, Category.SPORT);

        ArgumentCaptor<ParticipantJoinedEvent> body = ArgumentCaptor.forClass(ParticipantJoinedEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.EVENT_EXCHANGE), eq(RabbitMQConfig.EVENT_PARTICIPANT_JOINED_ROUTING_KEY), body.capture());
        assertThat(body.getValue().getEventId()).isEqualTo(eventId);
        assertThat(body.getValue().getUserId()).isEqualTo(userId);
        assertThat(body.getValue().getCategory()).isEqualTo(Category.SPORT);
        assertThat(body.getValue().getJoinedAt()).isNotNull();
    }
}
