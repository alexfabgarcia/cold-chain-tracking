package br.ufscar.ppgcc.data;

import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.Optional;

public class DeviceMeasurementEvent extends ApplicationEvent {

    private final Map<String, Double> measurements;

    public DeviceMeasurementEvent(Device device, Map<String, Double> measurements) {
        super(device);
        this.measurements = measurements;
    }

    public Device getDevice() {
        return Optional.of(source).map(Device.class::cast).orElseThrow(IllegalArgumentException::new);
    }

    public Map<String, Double> getMeasurements() {
        return measurements;
    }

}
