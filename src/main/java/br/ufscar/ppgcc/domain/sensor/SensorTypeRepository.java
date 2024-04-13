package br.ufscar.ppgcc.domain.sensor;

import br.ufscar.ppgcc.data.SensorType;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface SensorTypeRepository extends Repository<SensorType, UUID> {

    List<SensorType> findAllByOrderByName();

    SensorType findByName(String name);

}
