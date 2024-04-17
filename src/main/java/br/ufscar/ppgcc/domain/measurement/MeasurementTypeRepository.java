package br.ufscar.ppgcc.domain.measurement;

import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.MeasurementType;

import java.util.Collection;
import java.util.List;

public interface MeasurementTypeRepository extends GridCrudRepository<MeasurementType> {

    List<MeasurementType> findAllByOrderByName();

    MeasurementType findByName(String name);

    List<MeasurementType> findByNameIn(Collection<String> name);

}
