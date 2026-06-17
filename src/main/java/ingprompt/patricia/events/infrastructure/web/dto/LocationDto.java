package ingprompt.patricia.events.infrastructure.web.dto;

import ingprompt.patricia.events.domain.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private Double latitude;
    private Double longitude;
    private String address;
    private String placeId;

    public Location toDomain() {
        return new Location(latitude, longitude, address, placeId);
    }

    public static LocationDto from(Location location) {
        if (location == null) {
            return null;
        }
        return new LocationDto(
                location.getLatitude(),
                location.getLongitude(),
                location.getAddress(),
                location.getPlaceId()
        );
    }
}
