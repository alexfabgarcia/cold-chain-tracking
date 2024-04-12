package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.data.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

interface ProductRepository extends Repository<Product, UUID> {

    List<Product> findAll(Pageable pageable);

    long count(Specification<Product> specification);

    void delete(Product product);

    void save(Product product);

}
