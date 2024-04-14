package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.views.MainLayout;
import br.ufscar.ppgcc.domain.geolocation.GeolocationMarkerFeature;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

@PageTitle("Freights Measurements")
@Route(value = "freights/:id/measurements", layout = MainLayout.class)
public class FreightMeasurementView extends VerticalLayout implements BeforeEnterObserver {

    private final FreightDataProvider freightDataProvider;
    private final FreightFormLayout formLayout;

    public FreightMeasurementView(FreightDataProvider freightDataProvider, FreightFormLayout formLayout) {
        this.freightDataProvider = freightDataProvider;
        this.formLayout = formLayout;
        add(formLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var freight = event.getRouteParameters().get("id").map(UUID::fromString)
                .flatMap(freightDataProvider::findById)
                .orElseThrow(NotFoundException::new);

        formLayout.setReadOnly(freight);
        formLayout.addComponentAtIndex(4, new Paragraph("Device locations"));

        var deviceLocations = freightDataProvider.getDeviceLocations(freight);
        deviceLocations.forEach((key, value) -> formLayout.attachToMap(new GeolocationMarkerFeature(value, timeAgo(key))));
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
