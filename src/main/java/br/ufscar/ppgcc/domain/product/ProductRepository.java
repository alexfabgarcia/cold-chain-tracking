package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;

interface ProductRepository extends GridCrudRepository<Product> {

    @Override
    @EntityGraph("Product.sensorTypes")
    Slice<Product> findAll(Pageable pageable);

}
