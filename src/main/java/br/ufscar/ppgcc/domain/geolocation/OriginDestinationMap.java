package br.ufscar.ppgcc.domain.geolocation;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OriginDestinationMap {

    private final GeolocationComboBox origin;
    private final GeolocationComboBox destination;
    private final MarkerMap map;

    public OriginDestinationMap(GeolocationComboBox origin, GeolocationComboBox destination) {
        this.origin = origin;
        this.destination = destination;
        this.map = new MarkerMap();

        origin.setLabel("Origin");
        origin.setValueListenerMap(map);
        destination.setLabel("Destination");
        destination.setValueListenerMap(map);
    }

    public GeolocationComboBox getOrigin() {
        return origin;
    }

    public GeolocationComboBox getDestination() {
        return destination;
    }

    public MarkerMap getMap() {
        return map;
    }
}
