package br.ufscar.ppgcc.domain.geolocation;

import br.ufscar.ppgcc.common.component.AutocompleteComboBox;
import br.ufscar.ppgcc.data.GeolocationPoint;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GeolocationComboBox extends AutocompleteComboBox<GeolocationPoint> {

    private final MarkerFeature markerFeature;

    public GeolocationComboBox(GeolocationService geolocationService) {
        super(geolocationService::find);
        markerFeature = new MarkerFeature();

        setItemLabelGenerator(GeolocationPoint::address);
        setPlaceholder("Address or place");
        setPrefixComponent(VaadinIcon.MAP_MARKER.create());
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
        markerFeature.setText(label);
    }

    public void setValueListenerMap(MarkerMap map) {
        requireNonNull(map);
        addValueChangeListener(event -> {
            map.getFeatureLayer().removeFeature(markerFeature);
            if (nonNull(event.getValue())) {
                markerFeature.setCoordinates(new Coordinate(event.getValue().longitude(), event.getValue().latitude()));
                map.getFeatureLayer().addFeature(markerFeature);
            }
            map.zoomAndCenter();
        });
    }
}
