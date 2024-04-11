package br.ufscar.ppgcc.domain.carrier;

import br.ufscar.ppgcc.data.Carrier;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface CarrierRepository extends Repository<Carrier, UUID> {

    List<Carrier> findAllByOrderByFirstName();

}
