package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.Device;

import java.util.List;
import java.util.Optional;

interface DeviceRepository extends GridCrudRepository<Device> {

    List<Device> findTop10ByNameContainingIgnoreCase(String name);

    Optional<Device> findByExternalIdAndNetworkServer(String deviceExternalId, NetworkServer networkServer);

}
