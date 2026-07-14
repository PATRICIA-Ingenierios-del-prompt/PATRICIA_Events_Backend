package ingprompt.patricia.events.infrastructure.messaging.event;

import ingprompt.patricia.events.domain.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/** Outbound: a user joined an existing event — consumed by the achievements/album feature. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantJoinedEvent {
    private UUID eventId;
    private UUID userId;
    private Category category;
    private LocalDateTime joinedAt;
}
