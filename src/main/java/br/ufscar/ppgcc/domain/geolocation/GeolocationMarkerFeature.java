package br.ufscar.ppgcc.domain.geolocation;

import br.ufscar.ppgcc.data.GeolocationPoint;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.style.Icon;

import java.time.Duration;
import java.time.ZonedDateTime;

public class GeolocationMarkerFeature extends MarkerFeature {

    private static final Icon.Anchor ICON_ANCHOR = new Icon.Anchor(0.5, 0.9);
    private static final double ICON_OPACITY = 0.7;

    private final String textHolder;

    private boolean textVisible;

    public GeolocationMarkerFeature(GeolocationPoint point, ZonedDateTime zonedDateTime) {
        super(new Coordinate(point.longitude(), point.latitude()));
        this.textHolder = timeAgo(zonedDateTime);
        this.textVisible = false;
        setIcon(MarkerFeature.POINT_ICON);
        getIcon().setOpacity(ICON_OPACITY);
        getIcon().setAnchor(ICON_ANCHOR);
    }

    public void toggleText() {
        textVisible = !textVisible;
        setText(textVisible ? textHolder : null);
    }

    private String timeAgo(ZonedDateTime localDateTime) {
        var duration = Duration.between(localDateTime, ZonedDateTime.now());
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