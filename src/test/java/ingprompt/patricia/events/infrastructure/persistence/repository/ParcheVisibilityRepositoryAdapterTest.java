package ingprompt.patricia.events.infrastructure.persistence.repository;

import ingprompt.patricia.events.infrastructure.persistence.entity.ParcheVisibilityEntity;
import ingprompt.patricia.events.infrastructure.persistence.postgre.ParcheVisibilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParcheVisibilityRepositoryAdapterTest {

    @Mock
    private ParcheVisibilityRepository repository;

    private ParcheVisibilityRepositoryAdapter adapter;

    private final UUID parcheId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new ParcheVisibilityRepositoryAdapter(repository);
    }

    @Test
    void save_persistsEntityWithGivenFields() {
        adapter.save(parcheId, "Parche 4", "PUBLIC");

        ArgumentCaptor<ParcheVisibilityEntity> captor = ArgumentCaptor.forClass(ParcheVisibilityEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getParcheId()).isEqualTo(parcheId);
        assertThat(captor.getValue().getName()).isEqualTo("Parche 4");
        assertThat(captor.getValue().getVisibility()).isEqualTo("PUBLIC");
    }

    @Test
    void deleteByParcheId_delegatesToRepository() {
        adapter.deleteByParcheId(parcheId);

        verify(repository).deleteById(parcheId);
    }

    @Test
    void findNameById_whenPresent_returnsName() {
        ParcheVisibilityEntity entity = new ParcheVisibilityEntity(parcheId, "Parche 4", "PUBLIC");
        when(repository.findById(parcheId)).thenReturn(Optional.of(entity));

        assertThat(adapter.findNameById(parcheId)).contains("Parche 4");
    }

    @Test
    void findNameById_whenMissing_returnsEmpty() {
        when(repository.findById(parcheId)).thenReturn(Optional.empty());

        assertThat(adapter.findNameById(parcheId)).isEmpty();
    }
}
