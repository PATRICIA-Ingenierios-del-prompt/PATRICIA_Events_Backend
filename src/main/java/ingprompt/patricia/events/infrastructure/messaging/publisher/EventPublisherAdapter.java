package ingprompt.patricia.events.infrastructure.messaging.publisher;

import ingprompt.patricia.events.application.port.out.EventPublisherOut;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.infrastructure.messaging.config.RabbitMQConfig;
import ingprompt.patricia.events.infrastructure.messaging.event.EventCreatedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.EventDeletedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.EventEndedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.EventLinkedToParcheEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.EventStartedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.IncidentReportedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.ParticipantJoinedEvent;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Component
@AllArgsConstructor
public class EventPublisherAdapter implements EventPublisherOut {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishEventCreated(UUID eventId, String name, UUID ownerId, boolean linkedToParche, Category category) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EVENT_EXCHANGE,
                RabbitMQConfig.EVENT_CREATED_ROUTING_KEY,
                new EventCreatedEvent(UUID.randomUUID(), eventId, name, ownerId, linkedToParche, category)
        );
    }

    @Override
    public void publishEventLinkedToParche(UUID eventId, String eventName, UUID parcheId, String parcheName, UUID userId, Set<UUID> memberIds) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EVENT_EXCHANGE,
                RabbitMQConfig.EVENT_LINKED_ROUTING_KEY,
                new EventLinkedToParcheEvent(UUID.randomUUID(), eventId, eventName, parcheId, parcheName, userId, memberIds)
        );
    }

    @Override
    public void publishEventDeleted(UUID eventId, UUID parcheId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EVENT_EXCHANGE,
                RabbitMQConfig.EVENT_DELETED_ROUTING_KEY,
                new EventDeletedEvent(eventId, parcheId)
        );
    }

    @Override
    public void publishEventStarted(UUID eventId, Set<UUID> participants) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EVENT_EXCHANGE,
                RabbitMQConfig.EVENT_STARTED_ROUTING_KEY,
                new EventStartedEvent(eventId, participants)
        );
    }

    @Override
    public void publishEventEnded(UUID eventId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EVENT_EXCHANGE,
                RabbitMQConfig.EVENT_ENDED_ROUTING_KEY,
                new EventEndedEvent(eventId)
        );
    }

    @Override
    public void publishIncidentReported(UUID eventId, UUID reportId, UUID reporterId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EVENT_EXCHANGE,
                RabbitMQConfig.EVENT_INCIDENT_REPORTED_ROUTING_KEY,
                new IncidentReportedEvent(eventId, reportId, reporterId)
        );
    }

    @Override
    public void publishParticipantJoined(UUID eventId, UUID userId, Category category) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EVENT_EXCHANGE,
                RabbitMQConfig.EVENT_PARTICIPANT_JOINED_ROUTING_KEY,
                new ParticipantJoinedEvent(eventId, userId, category, LocalDateTime.now(Event.ZONE))
        );
    }
}
