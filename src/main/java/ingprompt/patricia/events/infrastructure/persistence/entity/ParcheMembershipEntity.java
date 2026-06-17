package ingprompt.patricia.events.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Local read model of which user is a member of which parche.
 * Fed by inbound Rabbit events from the Parches MS.
 */
@Entity
@Table(name = "parche_memberships")
@IdClass(ParcheMembershipId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcheMembershipEntity {
    @Id
    private UUID parcheId;

    @Id
    private UUID userId;
}
