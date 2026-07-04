package ingprompt.patricia.events.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Table(name = "parche_visibility")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcheVisibilityEntity {
    @Id
    private UUID parcheId;
    @Column
    private String name;

    @Column(nullable = false)
    private String visibility;
}
