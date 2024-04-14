package br.ufscar.ppgcc.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface GridCrudRepository<T> extends Repository<T, UUID> {

    Slice<T> findAll(Pageable pageable);

    long count(Specification<T> specification);

    void delete(T item);

    T save(T item);

    Optional<T> findById(UUID id);

}
