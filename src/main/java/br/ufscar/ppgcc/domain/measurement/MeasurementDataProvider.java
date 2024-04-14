package br.ufscar.ppgcc.domain.measurement;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.data.MeasurementType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class MeasurementDataProvider extends CrudDataProvider<MeasurementType> {

    private final MeasurementTypeRepository repository;

    MeasurementDataProvider(MeasurementTypeRepository repository) {
        super(repository);
        this.repository = repository;
    }

    List<MeasurementType> measurementTypes() {
        return repository.findAllByOrderByName();
    }
}
