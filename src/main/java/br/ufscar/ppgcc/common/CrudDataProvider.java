package br.ufscar.ppgcc.common;

import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class CrudDataProvider<T> extends AbstractBackEndDataProvider<T, CrudFilter> {

    private final GridCrudRepository<T> repository;

    public CrudDataProvider(GridCrudRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, CrudFilter> query) {
        return repository.findAll(getPageable(query)).stream();
    }

    private Pageable getPageable(Query<T, CrudFilter> query) {
        return PageRequest.of(query.getPage(), query.getPageSize(), getSort(query));
    }

    private Sort getSort(Query<T, CrudFilter> query) {
        var orders = query.getSortOrders().stream()
                .map(querySortOrder -> SortDirection.ASCENDING == querySortOrder.getDirection() ?
                        Sort.Order.asc(querySortOrder.getSorted()) : Sort.Order.desc(querySortOrder.getSorted()))
                .toList();
        return Sort.by(orders);
    }

    @Override
    protected int sizeInBackEnd(Query<T, CrudFilter> query) {
        return (int) repository.count(Specification.where(null));
    }

    public void save(T item) {
        repository.save(item);
    }

    public void delete(T item) {
        repository.delete(item);
    }

    public Optional<T> findById(UUID id) {
        return repository.findById(id);
    }

}
