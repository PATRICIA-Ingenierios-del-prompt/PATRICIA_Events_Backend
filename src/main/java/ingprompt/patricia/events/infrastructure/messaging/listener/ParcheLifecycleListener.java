package ingprompt.patricia.events.infrastructure.messaging.listener;

import ingprompt.patricia.events.application.port.in.ParcheMembershipCase;
import ingprompt.patricia.events.infrastructure.messaging.config.RabbitMQConfig;
import ingprompt.patricia.events.infrastructure.messaging.event.ParcheCreatedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.ParcheDeletedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.ParcheMemberExpelledEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.ParcheMemberJoinedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class ParcheLifecycleListener {
    private final ParcheMembershipCase membershipCase;

    @RabbitListener(queues = RabbitMQConfig.PARCHE_CREATED_QUEUE)
    public void onParcheCreated(ParcheCreatedEvent event) {
        log.info("Seeding membership for parche {} with owner {}", event.getParcheId(), event.getOwnerId());
        membershipCase.addMember(event.getParcheId(), event.getOwnerId());
    }

    @RabbitListener(queues = RabbitMQConfig.PARCHE_MEMBER_JOINED_QUEUE)
    public void onMemberJoined(ParcheMemberJoinedEvent event) {
        log.info("Adding member {} to parche {}", event.getMemberId(), event.getParcheId());
        membershipCase.addMember(event.getParcheId(), event.getMemberId());
    }

    @RabbitListener(queues = RabbitMQConfig.PARCHE_MEMBER_EXPELLED_QUEUE)
    public void onMemberExpelled(ParcheMemberExpelledEvent event) {
        log.info("Removing member {} from parche {}", event.getMemberId(), event.getParcheId());
        membershipCase.removeMember(event.getParcheId(), event.getMemberId());
    }

    @RabbitListener(queues = RabbitMQConfig.PARCHE_DELETED_QUEUE)
    public void onParcheDeleted(ParcheDeletedEvent event) {
        Set<UUID> eventIds = event.getEventIds();
        log.info("Cascading parche {} deletion ({} events linked)", event.getParcheId(), eventIds == null ? 0 : eventIds.size());
        membershipCase.handleParcheDeleted(event.getParcheId(), eventIds);
    }
}
