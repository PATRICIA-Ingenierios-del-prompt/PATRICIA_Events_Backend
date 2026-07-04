package ingprompt.patricia.events.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Emitted when an event is linked to a parche. Payload carries:
 *  - {@code userId}       : the user who created the linked event (kept for the
 *                           Parches MS consumer, which uses it to authorize the
 *                           link — the user must be a member of the parche).
 *  - {@code memberIds}    : the recipient set for the Notification MS.
 * The Notification MS ignores {@code userId} (Jackson INFERRED type precedence),
 * so both consumers are happy from the single payload.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventLinkedToParcheEvent {
    private UUID sourceEventId;
    private UUID eventId;
    private String eventName;
    private UUID parcheId;
    private String parcheName;
    /** Creator/linker — used by Parches MS to authorize the link. */
    private UUID userId;
    /** Notification MS recipient set (parche members at link time). */
    private Set<UUID> memberIds;
}
