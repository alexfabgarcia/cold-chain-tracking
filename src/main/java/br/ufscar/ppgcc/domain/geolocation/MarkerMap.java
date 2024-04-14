package br.ufscar.ppgcc.domain.geolocation;

import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.Feature;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.feature.PointBasedFeature;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

public class MarkerMap extends Map {

    private static final double EARTH_RADIUS_KM = 6371;
    private static final double SINGLE_POINT_ZOOM = 12;

    public MarkerMap() {
        addFeatureClickListener(event -> {
            if (event.getFeature() instanceof GeolocationMarkerFeature gmf) {
                gmf.toggleText();
            }
        });
    }

    public void attach(Feature markerFeature) {
        getFeatureLayer().addFeature(markerFeature);
    }

    public void zoomAndCenter() {
        var coordinates = getFeatureLayer().getFeatures().stream()
                .filter(MarkerFeature.class::isInstance)
                .map(MarkerFeature.class::cast)
                .map(PointBasedFeature::getCoordinates)
                .toList();

        if (!coordinates.isEmpty()) {
            var first = coordinates.get(0);

            if (coordinates.size() == 1) {
                setCenter(first);
                setZoom(SINGLE_POINT_ZOOM);
            } else {

                // Simplistic solution
                var minX = coordinates.stream().mapToDouble(Coordinate::getX).min().orElse(0);
                var minY = coordinates.stream().mapToDouble(Coordinate::getY).min().orElse(0);
                var maxX = coordinates.stream().mapToDouble(Coordinate::getX).max().orElse(0);
                var maxY = coordinates.stream().mapToDouble(Coordinate::getY).max().orElse(0);
                setZoom(zoomFrom(minX, minY, maxX, maxY));

                var centerX = (maxX + minX) / 2;
                var centerY = (maxY + minY) / 2;
                setCenter(new Coordinate(centerX, centerY));
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
