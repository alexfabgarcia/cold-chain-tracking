package br.ufscar.ppgcc.domain.measurement;

import br.ufscar.ppgcc.data.MeasurementType;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface MeasurementTypeRepository extends Repository<MeasurementType, UUID> {

    List<MeasurementType> findAllByOrderByName();

    MeasurementType findByName(String name);

}
