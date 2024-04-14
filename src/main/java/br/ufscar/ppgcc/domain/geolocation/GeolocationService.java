package br.ufscar.ppgcc.domain.geolocation;

import br.ufscar.ppgcc.data.GeolocationPoint;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GeolocationService {

    private final GeoApiContext geoApiContext;

    public GeolocationService(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    @Cacheable("location-search-cache")
    public List<GeolocationPoint> find(String searchText) {
        var response = GeocodingApi.geocode(geoApiContext, searchText).awaitIgnoreError();
        return Arrays.stream(response)
                .map(result -> {
                    var location = result.geometry.location;
                    return new GeolocationPoint(location.lat, location.lng, result.formattedAddress);
                })
                .toList();
    }

}
