package ingprompt.patricia.events.application.service;

import ingprompt.patricia.events.application.port.in.ParcheMembershipCase;
import ingprompt.patricia.events.application.port.out.EventRepositoryOutPort;
import ingprompt.patricia.events.application.port.out.ParcheMembershipRepositoryOutPort;
import ingprompt.patricia.events.application.port.out.ParcheVisibilityRepositoryOutPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ParcheMembershipService implements ParcheMembershipCase {
    private final ParcheMembershipRepositoryOutPort membershipRepository;
    private final ParcheVisibilityRepositoryOutPort visibilityRepository;
    private final EventRepositoryOutPort eventRepository;

    @Override
    @Transactional
    public void handleParcheCreated(UUID parcheId, UUID ownerId, String visibility) {
        visibilityRepository.save(parcheId, visibility);
        membershipRepository.save(parcheId, ownerId);
    }

    @Override
    @Transactional
    public void addMember(UUID parcheId, UUID userId) {
        membershipRepository.save(parcheId, userId);
    }

    @Override
    @Transactional
    public void removeMember(UUID parcheId, UUID userId) {
        membershipRepository.delete(parcheId, userId);
    }

    @Override
    @Transactional
    public void handleParcheDeleted(UUID parcheId, Set<UUID> eventIds) {
        membershipRepository.deleteAllByParcheId(parcheId);
        visibilityRepository.deleteByParcheId(parcheId);

        if (eventIds != null && !eventIds.isEmpty()) {
            eventRepository.deleteAllByIds(eventIds);
            log.info("Cascade-deleted {} events for parche {}", eventIds.size(), parcheId);
        }
    }
}
