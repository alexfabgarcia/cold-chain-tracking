package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.SensorType;
import br.ufscar.ppgcc.data.Product;
import br.ufscar.ppgcc.domain.sensor.SensorTypeRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class ProductDataProvider extends CrudDataProvider<Product> {

    private final SensorTypeRepository sensorTypeRepository;

    public ProductDataProvider(GridCrudRepository<Product> repository, SensorTypeRepository sensorTypeRepository) {
        super(repository);
        this.sensorTypeRepository = sensorTypeRepository;
    }

    public List<SensorType> sensorTypes() {
        return sensorTypeRepository.findAllByOrderByName();
    }

}
