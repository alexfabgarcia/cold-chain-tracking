package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.data.Device;
import br.ufscar.ppgcc.data.DeviceMeasurement;
import br.ufscar.ppgcc.data.GeolocationPoint;
import br.ufscar.ppgcc.data.MeasurementType;
import br.ufscar.ppgcc.domain.measurement.MeasurementTypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service
public class DeviceMeasurementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMeasurementService.class);

    private final DeviceMeasurementRepository deviceMeasurementRepository;
    private final MeasurementTypeRepository measurementTypeRepository;
    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;

    public DeviceMeasurementService(DeviceMeasurementRepository deviceMeasurementRepository,
                                    MeasurementTypeRepository measurementTypeRepository,
                                    DeviceRepository deviceRepository, ObjectMapper objectMapper) {
        this.deviceMeasurementRepository = deviceMeasurementRepository;
        this.measurementTypeRepository = measurementTypeRepository;
        this.deviceRepository = deviceRepository;
        this.objectMapper = objectMapper;
    }

    public void savePayload(String deviceExternalId, NetworkServer networkServer, String measurement,
                            ZonedDateTime moment) {
        var device = getDevice(deviceExternalId, networkServer);
        // TODO treat payload format
        var temperature = measurementTypeRepository.findByName("Temperature");
        var deviceMeasurement = new DeviceMeasurement(device, temperature, moment, measurement);
        deviceMeasurementRepository.save(deviceMeasurement);
    }

    private Device getDevice(String deviceExternalId, NetworkServer networkServer) {
        return deviceRepository.findByExternalIdAndNetworkServer(deviceExternalId, networkServer)
                .orElseGet(() -> deviceRepository.save(new Device(deviceExternalId, networkServer)));
    }

    public void saveLocation(String deviceExternalId, NetworkServer networkServer, GeolocationPoint geolocationPoint,
                             ZonedDateTime time) {
        var device = getDevice(deviceExternalId, networkServer);
        var deviceMeasurement = new DeviceMeasurement(device, locationMeasurement(), time, writeJsonSafely(geolocationPoint));
        deviceMeasurementRepository.save(deviceMeasurement);
    }

    private String writeJsonSafely(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            LOGGER.error("An error occurred while writing JSON.", e);
            return payload.toString();
        }
    }

    private MeasurementType locationMeasurement() {
        return measurementTypeRepository.findByName("Location");
    }

    public Map<MeasurementType, List<DeviceMeasurement>> getSensorMeasurements(Device device, ZonedDateTime start, ZonedDateTime end) {
        return deviceMeasurementRepository.findByDeviceAndMeasurementTypeIsNotAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                device, locationMeasurement(), start, end).stream()
                .collect(groupingBy(DeviceMeasurement::getMeasurementType));
    }

    public Map<ZonedDateTime, GeolocationPoint> getLocations(Device device, ZonedDateTime start, ZonedDateTime end) {
        return deviceMeasurementRepository.findByDeviceAndMeasurementTypeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                device, locationMeasurement(), start, end)
                .stream()
                .collect(toMap(DeviceMeasurement::getMeasuredAt, this::readGeolocationPoint));
    }

    private GeolocationPoint readGeolocationPoint(DeviceMeasurement deviceMeasurement) {
        try {
            return objectMapper.readValue(deviceMeasurement.getValue(), GeolocationPoint.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
