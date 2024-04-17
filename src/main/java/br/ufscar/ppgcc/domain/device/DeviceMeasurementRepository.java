package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.data.Device;
import br.ufscar.ppgcc.data.DeviceMeasurement;
import br.ufscar.ppgcc.data.MeasurementType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface DeviceMeasurementRepository extends Repository<DeviceMeasurement, UUID> {

    void save(DeviceMeasurement deviceMeasurement);

    void saveAll(Iterable<DeviceMeasurement> deviceMeasurement);

    @EntityGraph("DeviceMeasurement.eager")
    List<DeviceMeasurement> findByDeviceAndMeasurementTypeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            Device device, MeasurementType measurementType, ZonedDateTime start, ZonedDateTime end);

    @EntityGraph("DeviceMeasurement.eager")
    List<DeviceMeasurement> findByDeviceAndMeasurementTypeIsNotAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            Device device, MeasurementType measurementType, ZonedDateTime start, ZonedDateTime end);

}
