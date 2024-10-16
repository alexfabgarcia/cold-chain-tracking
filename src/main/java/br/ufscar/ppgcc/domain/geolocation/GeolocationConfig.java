package br.ufscar.ppgcc.domain.geolocation;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GeolocationConfig {

    @Bean
    GeoApiContext geoApiContext(@Value("${google.geolocation.api-key}") String apiKey) {
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

}
