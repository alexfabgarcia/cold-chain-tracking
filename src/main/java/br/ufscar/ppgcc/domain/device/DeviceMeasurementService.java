package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.data.DeviceMeasurement;
import br.ufscar.ppgcc.domain.sensor.SensorTypeRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class DeviceMeasurementService {

    private final DeviceMeasurementRepository deviceMeasurementRepository;
    private final SensorTypeRepository sensorTypeRepository;

    public DeviceMeasurementService(DeviceMeasurementRepository deviceMeasurementRepository,
                                    SensorTypeRepository sensorTypeRepository) {
        this.deviceMeasurementRepository = deviceMeasurementRepository;
        this.sensorTypeRepository = sensorTypeRepository;
    }

    public void saveTemperature(String deviceId, String measurement, ZonedDateTime moment) {
        var temperature = sensorTypeRepository.findByName("Temperature");
        var deviceMeasurement = new DeviceMeasurement(deviceId, temperature, moment, measurement);
        deviceMeasurementRepository.save(deviceMeasurement);
    }

    public void saveLocation(String deviceId, String data, ZonedDateTime time) {
        var location = sensorTypeRepository.findByName("Location");
        var deviceMeasurement = new DeviceMeasurement(deviceId, location, time, data);
        deviceMeasurementRepository.save(deviceMeasurement);
    }
}
