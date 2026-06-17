package ingprompt.patricia.events.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcheMembershipId implements Serializable {
    private UUID parcheId;
    private UUID userId;
}
