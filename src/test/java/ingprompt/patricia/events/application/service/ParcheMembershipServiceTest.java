package ingprompt.patricia.events.application.service;

import ingprompt.patricia.events.application.port.out.EventRepositoryOutPort;
import ingprompt.patricia.events.application.port.out.ParcheMembershipRepositoryOutPort;
import ingprompt.patricia.events.application.port.out.ParcheVisibilityRepositoryOutPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ParcheMembershipServiceTest {

    @Mock
    private ParcheMembershipRepositoryOutPort membershipRepository;
    @Mock
    private ParcheVisibilityRepositoryOutPort visibilityRepository;
    @Mock
    private EventRepositoryOutPort eventRepository;

    @InjectMocks
    private ParcheMembershipService service;

    private final UUID parcheId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void handleParcheCreated_recordsVisibilityAndSeedsOwner() {
        service.handleParcheCreated(parcheId, userId, "PUBLIC");

        verify(visibilityRepository).save(parcheId, "PUBLIC");
        verify(membershipRepository).save(parcheId, userId);
    }

    @Test
    void addMember_persistsMembership() {
        service.addMember(parcheId, userId);
        verify(membershipRepository).save(parcheId, userId);
    }

    @Test
    void removeMember_deletesMembership() {
        service.removeMember(parcheId, userId);
        verify(membershipRepository).delete(parcheId, userId);
    }

    @Test
    void handleParcheDeleted_withEventIds_cascades() {
        Set<UUID> eventIds = Set.of(UUID.randomUUID(), UUID.randomUUID());

        service.handleParcheDeleted(parcheId, eventIds);

        verify(membershipRepository).deleteAllByParcheId(parcheId);
        verify(eventRepository).deleteAllByIds(eventIds);
    }

    @Test
    void handleParcheDeleted_withoutEventIds_onlyDropsMemberships() {
        service.handleParcheDeleted(parcheId, Set.of());

        verify(membershipRepository).deleteAllByParcheId(parcheId);
        verify(eventRepository, never()).deleteAllByIds(any());
    }
}
