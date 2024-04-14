package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.data.Freight;
import org.springframework.stereotype.Component;

@Component
class FreightDataProvider extends CrudDataProvider<Freight> {

    public FreightDataProvider(FreightRepository repository) {
        super(repository);
    }

}
