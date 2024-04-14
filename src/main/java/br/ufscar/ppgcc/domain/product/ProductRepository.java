package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

interface ProductRepository extends GridCrudRepository<Product> {

    @Override
    @EntityGraph("Product.measurementTypes")
    Slice<Product> findAll(Pageable pageable);

    List<Product> findTop10ByNameContainingIgnoreCase(String name);

}
