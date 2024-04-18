package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.Device;
import br.ufscar.ppgcc.data.Freight;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface FreightRepository extends GridCrudRepository<Freight> {

    @Override
    @EntityGraph("Freight.details")
    Slice<Freight> findAll(Pageable pageable);

    @EntityGraph("Freight.details")
    Slice<Freight> findByCarrierUserId(String userId, Pageable pageable);

    @EntityGraph("Freight.productMeasurementsAndDetails")
    Optional<Freight> findFirstByDeviceAndStartedAtIsNotNullAndFinishedAtIsNullOrderByCreatedAtDesc(Device device);

}
