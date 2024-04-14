package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.data.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class ProductDataProvider extends CrudDataProvider<Product> {

    private final ProductRepository repository;

    ProductDataProvider(ProductRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public List<Product> search(String searchText) {
        return repository.findTop10ByNameContainingIgnoreCase(searchText);
    }

}
