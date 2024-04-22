package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.views.MainLayout;
import br.ufscar.ppgcc.data.DeviceMeasurement;
import br.ufscar.ppgcc.data.Freight;
import br.ufscar.ppgcc.data.MeasurementType;
import br.ufscar.ppgcc.data.ProductMeasurementType;
import br.ufscar.ppgcc.domain.geolocation.GeolocationMarkerFeature;
import br.ufscar.ppgcc.domain.geolocation.OriginDestinationMap;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@RolesAllowed({"ADMIN", "CARRIER"})
@PermitAll
@PageTitle("Freights Measurements")
@Route(value = "freights/:id/measurements", layout = MainLayout.class)
public class FreightMeasurementView extends VerticalLayout implements BeforeEnterObserver {

    private final FreightDataProvider freightDataProvider;
    private final FreightFormLayout formLayout;

    public FreightMeasurementView(FreightDataProvider freightDataProvider, FreightFormLayout formLayout) {
        this.freightDataProvider = freightDataProvider;
        this.formLayout = formLayout;
        add(formLayout);
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var freight = event.getRouteParameters().get("id").map(UUID::fromString)
                .flatMap(freightDataProvider::findById)
                .orElseThrow(NotFoundException::new);

        var map = locationsMap(freight);
        map.setSizeFull();
        formLayout.setReadOnly(freight);

        var tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.add("Location", map);

        var measurementMap = freightDataProvider.getSensorMeasurements(freight);

        if (!measurementMap.isEmpty()) {
            var productMeasurements = freightDataProvider.getProductMeasurements(freight);
            measurementMap.forEach((key, value) -> tabs.add(key.getName(), measurementChart(key, value, productMeasurements.get(key))));
            add(tabs);
        }
    }

    private Map locationsMap(Freight freight) {
        var map = new OriginDestinationMap(formLayout.getOriginComboBox(), formLayout.getDestinationComboBox());
        var deviceLocations = freightDataProvider.getDeviceLocations(freight);
        deviceLocations.forEach((key, value) -> map.attach(new GeolocationMarkerFeature(value, key)));
        return map;
    }

    private Chart measurementChart(MeasurementType measurementType, List<DeviceMeasurement> measurements,
                                   ProductMeasurementType productMeasurementType) {
        var chart = new Chart(ChartType.SPLINE);
        chart.setTimeline(true);
        chart.setSizeFull();
        var configuration = chart.getConfiguration();
        configuration.getNavigator().setEnabled(false);
        configuration.getScrollbar().setEnabled(false);

        configuration.getTooltip().setValueSuffix(measurementType.getMeasurementUnit());
        configuration.getLegend().setEnabled(false);

        var xAxis = configuration.getxAxis();
        xAxis.setType(AxisType.DATETIME);
        var yAxis = configuration.getyAxis();
        yAxis.setTitle(new AxisTitle(measurementType.getName()));

        var series = new DataSeries();
        series.setName(measurementType.getName());
        measurements.forEach(measurement -> {
            var value = Double.valueOf(measurement.getValue());
            var item = new DataSeriesItem(measurement.getMeasuredAt().toInstant(), value);
            Optional.ofNullable(productMeasurementType).ifPresent(limits -> {
                if (value < limits.getMinimum() || value > limits.getMaximum()) {
                    item.setColor(SolidColor.RED); // TODO how to?
                }
            });
            series.add(item);
        });
        configuration.addSeries(series);

        if (nonNull(productMeasurementType)) {
            var plotOptions = new PlotOptionsSpline();
            plotOptions.setSoftThreshold(false);
            plotOptions.setThreshold(productMeasurementType.getMinimum());
            plotOptions.setNegativeColor(SolidColor.RED);
            configuration.setPlotOptions(plotOptions);

            var thresholdLabel = new Label(String.format("%s range for %s", measurementType.getName(), productMeasurementType.getProduct().getName()));
            thresholdLabel.setAlign(HorizontalAlign.CENTER);
            var thresholds = new PlotBand(productMeasurementType.getMinimum(), productMeasurementType.getMaximum(), SolidColor.ALICEBLUE);
            thresholds.setLabel(thresholdLabel);
            thresholds.setBorderWidth(1);
            thresholds.setZIndex(1);
            yAxis.addPlotBand(thresholds);
            yAxis.setSoftMin(productMeasurementType.getMinimum());
            yAxis.setSoftMax(productMeasurementType.getMaximum());
        }

        return chart;
    }

}
