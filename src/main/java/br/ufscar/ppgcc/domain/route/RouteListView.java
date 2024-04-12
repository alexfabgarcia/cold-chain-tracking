package br.ufscar.ppgcc.domain.route;

import br.ufscar.ppgcc.views.MainLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.feature.PointBasedFeature;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.function.Function;

import static java.lang.Math.*;
import static java.util.Objects.nonNull;

@PageTitle("Routes")
@Route(value = "routes", layout = MainLayout.class)
public class RouteListView extends VerticalLayout {

    static class MarkerMap extends Map {

        private static final double EARTH_RADIUS_KM = 6371;

        void zoomAndCenter() {
            var coordinates = getFeatureLayer().getFeatures().stream()
                    .filter(MarkerFeature.class::isInstance)
                    .map(MarkerFeature.class::cast)
                    .map(PointBasedFeature::getCoordinates)
                    .toList();

            if (!coordinates.isEmpty()) {
                var first = coordinates.get(0);

                if (coordinates.size() == 1) {
                    setCenter(first);
                    setZoom(12);
                } else {

                    // Simplistic solution, I know
                    var minX = coordinates.stream().mapToDouble(Coordinate::getX).min().orElse(0);
                    var minY = coordinates.stream().mapToDouble(Coordinate::getY).min().orElse(0);
                    var maxX = coordinates.stream().mapToDouble(Coordinate::getX).max().orElse(0);
                    var maxY = coordinates.stream().mapToDouble(Coordinate::getY).max().orElse(0);

                    var centerX = (maxX + minX) / 2;
                    var centerY = (maxY + minY) / 2;

                    setCenter(new Coordinate(centerX, centerY));
                    setZoom(zoomFrom(minX, minY, maxX, maxY));
                }
            }
        }

        // TODO create logarithm function to simulate it properly
        private int zoomFrom(double minX, double minY, double maxX, double maxY) {
            double distance = distance(minX, minY, maxX, maxY);
            if (distance >= 10000) {
                return 2;
            } else if (distance >= 2000) {
                return 4;
            } else if (distance >= 600) {
                return 6;
            } else if (distance >= 100) {
                return 8;
            } else if (distance >= 20) {
                return 10;
            } else if (distance >= 4) {
                return 12;
            }
            return 14;
        }

        private double distance(double minX, double minY, double maxX, double maxY) {

            // acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon2-lon1)) * 6371 (6371 is Earth radius in km and lat long as radians.)
            return acos(sin(toRadians(minY)) * sin(toRadians(maxY)) + cos(toRadians(minY)) * cos(toRadians(maxY)) * cos(toRadians(maxX) - toRadians(minX))) * EARTH_RADIUS_KM;
        }
    }

    @SuppressWarnings("java:S110")
    static class ComboBoxGeolocation extends ComboBox<GeolocationPoint> {

        private final MarkerFeature markerFeature = new MarkerFeature();

        ComboBoxGeolocation(String label, Function<String, List<GeolocationPoint>> geolocationSearch, MarkerMap map) {
            super(label);
            markerFeature.setText(label);

            setItemLabelGenerator(GeolocationPoint::address);
            setPlaceholder("Address or place");
            setHelperText("Type at least 5 characters and press enter");
            setClearButtonVisible(true);
            setPrefixComponent(VaadinIcon.MAP_MARKER.create());
            setAllowCustomValue(true);
            setManualValidation(true);

            addCustomValueSetListener(input -> {
                var customValue = input.getDetail();
                if (nonNull(customValue) && customValue.length() > 4) {
                    setItems(geolocationSearch.apply(customValue));
                    setInvalid(false);
                    setOpened(true);
                } else {
                    setErrorMessage("Invalid input text.");
                    setInvalid(true);
                }
            });

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

    public RouteListView(GeolocationService geolocationService) {
        var map = new MarkerMap();
        map.setSizeFull();

        Function<String, List<GeolocationPoint>> geolocationSearch = geolocationService::find;
        var origin = new ComboBoxGeolocation("Origin", geolocationSearch, map);
        var destination = new ComboBoxGeolocation("Destination", geolocationSearch, map);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep(LumoUtility.MinWidth.NONE, 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep(LumoUtility.MaxWidth.SCREEN_MEDIUM, 2));

        formLayout.add(origin, destination);

        setSizeFull();
        add(formLayout, map);
    }

}
