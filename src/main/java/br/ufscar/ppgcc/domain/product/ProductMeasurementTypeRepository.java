package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.data.Product;
import br.ufscar.ppgcc.data.ProductMeasurementType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface ProductMeasurementTypeRepository extends Repository<ProductMeasurementType, UUID> {

    @EntityGraph("ProductMeasurementType.eager")
    List<ProductMeasurementType> findByProduct(Product product);

}
