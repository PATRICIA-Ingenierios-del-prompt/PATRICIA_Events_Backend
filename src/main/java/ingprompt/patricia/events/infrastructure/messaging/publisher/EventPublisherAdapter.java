package ingprompt.patricia.events.infrastructure.messaging.publisher;

import ingprompt.patricia.events.application.port.out.EventPublisherOut;
import ingprompt.patricia.events.infrastructure.messaging.config.RabbitMQConfig;
import ingprompt.patricia.events.infrastructure.messaging.event.EventDeletedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.EventLinkedToParcheEvent;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class EventPublisherAdapter implements EventPublisherOut {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishEventLinkedToParche(UUID eventId, UUID parcheId, UUID userId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EVENT_EXCHANGE,
                RabbitMQConfig.EVENT_LINKED_ROUTING_KEY,
                new EventLinkedToParcheEvent(eventId, parcheId, userId)
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
}
