package ingprompt.patricia.events.infrastructure.persistence.repository;

import ingprompt.patricia.events.infrastructure.persistence.entity.ParcheMembershipEntity;
import ingprompt.patricia.events.infrastructure.persistence.entity.ParcheMembershipId;
import ingprompt.patricia.events.infrastructure.persistence.postgre.ParcheMembershipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParcheMembershipRepositoryAdapterTest {

    @Mock
    private ParcheMembershipRepository repository;

    private ParcheMembershipRepositoryAdapter adapter;

    private final UUID parcheId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new ParcheMembershipRepositoryAdapter(repository);
    }

    @Test
    void save_persistsMembershipEntity() {
        adapter.save(parcheId, userId);

        ArgumentCaptor<ParcheMembershipEntity> captor = ArgumentCaptor.forClass(ParcheMembershipEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getParcheId()).isEqualTo(parcheId);
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
    }

    @Test
    void delete_removesByCompositeId() {
        adapter.delete(parcheId, userId);

        verify(repository).deleteById(new ParcheMembershipId(parcheId, userId));
    }

    @Test
    void deleteAllByParcheId_delegatesToRepository() {
        adapter.deleteAllByParcheId(parcheId);

        verify(repository).deleteAllByParcheId(parcheId);
    }

    @Test
    void exists_delegatesToRepository() {
        when(repository.existsById(new ParcheMembershipId(parcheId, userId))).thenReturn(true);

        assertThat(adapter.exists(parcheId, userId)).isTrue();
    }

    @Test
    void findParcheIdsByUser_returnsSet() {
        UUID other = UUID.randomUUID();
        when(repository.findParcheIdsByUserId(userId)).thenReturn(List.of(parcheId, other));

        assertThat(adapter.findParcheIdsByUser(userId)).containsExactlyInAnyOrder(parcheId, other);
    }

    @Test
    void findUserIdsByParcheId_returnsSet() {
        UUID other = UUID.randomUUID();
        when(repository.findUserIdsByParcheId(parcheId)).thenReturn(List.of(userId, other));

        assertThat(adapter.findUserIdsByParcheId(parcheId)).containsExactlyInAnyOrder(userId, other);
    }
}
