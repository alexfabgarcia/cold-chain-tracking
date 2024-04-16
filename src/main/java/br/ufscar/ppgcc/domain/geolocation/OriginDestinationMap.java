package br.ufscar.ppgcc.domain.geolocation;

public class OriginDestinationMap extends MarkerMap {

    public OriginDestinationMap(GeolocationComboBox origin, GeolocationComboBox destination) {
        origin.setLabel("Origin");
        origin.setValueListenerMap(this);
        destination.setLabel("Destination");
        destination.setValueListenerMap(this);
    }

}
