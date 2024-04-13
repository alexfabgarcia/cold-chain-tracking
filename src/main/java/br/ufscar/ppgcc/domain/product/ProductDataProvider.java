package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.Product;
import org.springframework.stereotype.Component;

@Component
class ProductDataProvider extends CrudDataProvider<Product> {

    public ProductDataProvider(GridCrudRepository<Product> repository) {
        super(repository);
    }

}
