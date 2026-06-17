package ingprompt.patricia.events.infrastructure.persistence.entity;

import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.model.Location;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
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

    private UUID parcheId;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean started;

    @Column(nullable = false)
    private boolean finished;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "meeting_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "meeting_longitude")),
            @AttributeOverride(name = "address", column = @Column(name = "meeting_address")),
            @AttributeOverride(name = "placeId", column = @Column(name = "meeting_place_id"))
    })
    private Location meetingPoint;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "destination_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "destination_longitude")),
            @AttributeOverride(name = "address", column = @Column(name = "destination_address")),
            @AttributeOverride(name = "placeId", column = @Column(name = "destination_place_id"))
    })
    private Location destination;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_participants", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "user_id")
    private Set<UUID> usersInscribed;
}
