package br.ufscar.ppgcc.data;

import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

public class DeviceMeasurementEvent extends ApplicationEvent {

    private final Map<String, Double> measurements;

    public DeviceMeasurementEvent(Device device, Map<String, String> measurements) {
        super(device);
        this.measurements = measurements.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> Double.valueOf(entry.getValue())));
    }

    public Device getDevice() {
        return Optional.of(source).map(Device.class::cast).orElseThrow(IllegalArgumentException::new);
    }

    public Map<String, Double> getMeasurements() {
        return measurements;
    }

}
