package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.data.DeviceMeasurement;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface DeviceMeasurementRepository extends Repository<DeviceMeasurement, UUID> {

    DeviceMeasurement save(DeviceMeasurement deviceMeasurement);

}
