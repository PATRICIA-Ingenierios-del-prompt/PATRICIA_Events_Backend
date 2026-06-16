package ingprompt.patricia.events.infrastructure.persistence.entity;

import ingprompt.patricia.events.domain.enums.Category;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
public class EventEntity {
    @Id
    private UUID eventId;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private int maxCapacity;

    // null when the event isn't linked to any parche
    private UUID parcheId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_participants", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "user_id")
    private Set<UUID> usersInscribed;
}
