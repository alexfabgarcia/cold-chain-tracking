package br.ufscar.ppgcc.domain.carrier;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.Carrier;
import org.springframework.stereotype.Component;

@Component
class CarrierDataProvider extends CrudDataProvider<Carrier> {

    public CarrierDataProvider(GridCrudRepository<Carrier> repository) {
        super(repository);
    }

}
