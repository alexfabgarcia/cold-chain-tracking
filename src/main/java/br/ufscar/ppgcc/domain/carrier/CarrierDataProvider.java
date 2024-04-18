package br.ufscar.ppgcc.domain.carrier;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.data.Carrier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class CarrierDataProvider extends CrudDataProvider<Carrier> {

    private final transient CarrierRepository repository;

    public CarrierDataProvider(CarrierRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public List<Carrier> search(String searchText) {
        return repository.findTop10ByFirstNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(searchText, searchText);
    }

}
