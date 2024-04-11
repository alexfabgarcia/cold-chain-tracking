package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.data.DeviceMeasurement;
import br.ufscar.ppgcc.domain.measurement.MeasurementTypeRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class DeviceMeasurementService {

    private final DeviceMeasurementRepository deviceMeasurementRepository;
    private final MeasurementTypeRepository measurementTypeRepository;

    public DeviceMeasurementService(DeviceMeasurementRepository deviceMeasurementRepository, MeasurementTypeRepository measurementTypeRepository) {
        this.deviceMeasurementRepository = deviceMeasurementRepository;
        this.measurementTypeRepository = measurementTypeRepository;
    }

    public void saveTemperature(String deviceId, String measurement, ZonedDateTime moment) {
        var temperature = measurementTypeRepository.findByName("Temperature");
        var deviceMeasurement = new DeviceMeasurement(deviceId, temperature, moment, measurement);
        deviceMeasurementRepository.save(deviceMeasurement);
    }

    public void saveLocation(String deviceId, String data, ZonedDateTime time) {
        var location = measurementTypeRepository.findByName("Location");
        var deviceMeasurement = new DeviceMeasurement(deviceId, location, time, data);
        deviceMeasurementRepository.save(deviceMeasurement);
    }
}
