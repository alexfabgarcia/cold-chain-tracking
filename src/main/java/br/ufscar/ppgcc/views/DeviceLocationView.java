package br.ufscar.ppgcc.views;

import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.style.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.Duration;
import java.time.LocalDateTime;

public class DeviceLocationView extends VerticalLayout {

    static class CustomMarkerFeature extends MarkerFeature {

        private final String textHolder;
        private boolean textVisible;

        CustomMarkerFeature(Coordinate coordinate, String text) {
            super(coordinate);
            this.textHolder = text;
            this.textVisible = false;
        }

        public void toggleText() {
            textVisible = !textVisible;
            setText(textVisible ? textHolder : null);
        }
    }

    public DeviceLocationView() {
        var deviceCoordinate = new Coordinate(5.16015, 52.2718);
        var gatewayCoordinate = new Coordinate(5.16445406, 52.24261292);

        var deviceMarker = new CustomMarkerFeature(deviceCoordinate, timeAgo(LocalDateTime.now()));
        deviceMarker.setIcon(MarkerFeature.POINT_ICON);
        deviceMarker.getIcon().setOpacity(0.7);
        deviceMarker.getIcon().setAnchor(new Icon.Anchor(0.5, 0.9));

        var gatewayMarker = new MarkerFeature(gatewayCoordinate);
        gatewayMarker.setText("Received " + timeAgo(LocalDateTime.now()));

        var map = new Map();
        map.getFeatureLayer().addFeature(deviceMarker);
        map.getFeatureLayer().addFeature(gatewayMarker);
        map.addFeatureClickListener(e -> {
            if (e.getFeature() instanceof CustomMarkerFeature m) {
                m.toggleText();
            }
        });

        map.setCenter(deviceCoordinate);
        map.setZoom(12);
        map.setHeightFull();

        setSizeFull();
        add(map);
    }

    private String timeAgo(LocalDateTime localDateTime) {
        var duration = Duration.between(localDateTime, LocalDateTime.now());
        if (duration.toMinutesPart() == 0) {
            return "just now";
        }
        if (duration.toHoursPart() == 0) {
            return duration.toMinutes() + " minute(s) ago";
        }
        if (duration.toDaysPart() == 0) {
            return duration.toHours() + " hour(s) ago";
        }
        return duration.toHours() + " day(s) ago";
    }
}
