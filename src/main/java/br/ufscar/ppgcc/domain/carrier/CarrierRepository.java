package br.ufscar.ppgcc.domain.carrier;

import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.Carrier;

import java.util.List;
import java.util.Optional;

public interface CarrierRepository extends GridCrudRepository<Carrier> {

    Optional<Carrier> findByUserId(String userId);

    List<Carrier> findTop10ByFirstNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String firstNameSearch,
                                                                                        String surnameSearch);

}
