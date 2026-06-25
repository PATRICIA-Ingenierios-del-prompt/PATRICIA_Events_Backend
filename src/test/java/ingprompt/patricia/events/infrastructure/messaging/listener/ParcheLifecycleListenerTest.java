package ingprompt.patricia.events.infrastructure.messaging.listener;

import ingprompt.patricia.events.application.port.in.ParcheMembershipCase;
import ingprompt.patricia.events.infrastructure.messaging.event.ParcheCreatedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.ParcheDeletedEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.ParcheMemberExpelledEvent;
import ingprompt.patricia.events.infrastructure.messaging.event.ParcheMemberJoinedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ParcheLifecycleListenerTest {

    @Mock
    private ParcheMembershipCase membershipCase;
    @InjectMocks
    private ParcheLifecycleListener listener;

    private final UUID parcheId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void onParcheCreated_seedsOwnerMembership() {
        listener.onParcheCreated(new ParcheCreatedEvent(parcheId, userId));
        verify(membershipCase).addMember(parcheId, userId);
    }

    @Test
    void onMemberJoined_addsMember() {
        listener.onMemberJoined(new ParcheMemberJoinedEvent(parcheId, userId));
        verify(membershipCase).addMember(parcheId, userId);
    }

    @Test
    void onMemberExpelled_removesMember() {
        listener.onMemberExpelled(new ParcheMemberExpelledEvent(parcheId, userId));
        verify(membershipCase).removeMember(parcheId, userId);
    }

    @Test
    void onParcheDeleted_cascades() {
        Set<UUID> eventIds = Set.of(UUID.randomUUID());
        listener.onParcheDeleted(new ParcheDeletedEvent(parcheId, userId, eventIds));
        verify(membershipCase).handleParcheDeleted(parcheId, eventIds);
    }
}
