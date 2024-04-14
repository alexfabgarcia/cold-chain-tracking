package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.data.Device;
import br.ufscar.ppgcc.data.DeviceMeasurement;
import br.ufscar.ppgcc.data.MeasurementType;
import org.springframework.data.repository.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface DeviceMeasurementRepository extends Repository<DeviceMeasurement, UUID> {

    DeviceMeasurement save(DeviceMeasurement deviceMeasurement);

    List<DeviceMeasurement> findByDeviceAndMeasurementTypeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            Device device, MeasurementType measurementType, ZonedDateTime start, ZonedDateTime end);
}
