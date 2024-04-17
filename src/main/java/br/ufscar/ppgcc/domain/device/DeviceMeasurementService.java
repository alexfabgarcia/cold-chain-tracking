package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.data.*;
import br.ufscar.ppgcc.domain.measurement.MeasurementTypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.PathMatcher;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service
public class DeviceMeasurementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMeasurementService.class);

    private final DeviceMeasurementRepository deviceMeasurementRepository;
    private final MeasurementTypeRepository measurementTypeRepository;
    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;
    private final PathMatcher pathMatcher;
    private final ApplicationEventPublisher applicationEventPublisher;

    public DeviceMeasurementService(DeviceMeasurementRepository deviceMeasurementRepository,
                                    MeasurementTypeRepository measurementTypeRepository,
                                    DeviceRepository deviceRepository, ObjectMapper objectMapper, PathMatcher pathMatcher, ApplicationEventPublisher applicationEventPublisher) {
        this.deviceMeasurementRepository = deviceMeasurementRepository;
        this.measurementTypeRepository = measurementTypeRepository;
        this.deviceRepository = deviceRepository;
        this.objectMapper = objectMapper;
        this.pathMatcher = pathMatcher;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void savePayload(String deviceExternalId, NetworkServer networkServer, String deviceEui, String payload,
                            ZonedDateTime moment) {
        var device = getDevice(deviceExternalId, networkServer, deviceEui);

        if (pathMatcher.match(device.getPayloadPattern(), payload)) {
            var measurements = pathMatcher.extractUriTemplateVariables(device.getPayloadPattern(), payload);
            var types = measurementTypeRepository.findByNameIn(measurements.keySet()).stream().collect(toMap(MeasurementType::getName, Function.identity()));
            var deviceMeasurements = measurements.entrySet().stream()
                    .map(entry -> new DeviceMeasurement(device, types.get(entry.getKey()), moment, entry.getValue()))
                    .toList();

            deviceMeasurementRepository.saveAll(deviceMeasurements);
            applicationEventPublisher.publishEvent(new DeviceMeasurementEvent(device, measurements));
        } else {
            var deviceMeasurement = new DeviceMeasurement(device, moment, payload);
            deviceMeasurementRepository.save(deviceMeasurement);
            LOGGER.warn("Device pattern does not match received payload. External id: {}, Pattern: {}",
                    device.getExternalId(), device.getPayloadPattern());
        }
    }

    private Device getDevice(String deviceExternalId, NetworkServer networkServer, String deviceEui) {
        return deviceRepository.findByExternalIdAndNetworkServer(deviceExternalId, networkServer)
                .map(device -> {
                    if (isNull(device.getEui()) && nonNull(deviceEui)) {
                        device.setEui(deviceEui);
                        return deviceRepository.save(device);
                    }
                    return device;
                })
                .orElseGet(() -> deviceRepository.save(new Device(deviceExternalId, networkServer)));
    }

    public void saveLocation(String deviceExternalId, NetworkServer networkServer, String deviceEui,
                             GeolocationPoint geolocationPoint, ZonedDateTime time) {
        var device = getDevice(deviceExternalId, networkServer, deviceEui);
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
