package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.Device;

import java.util.List;

interface DeviceRepository extends GridCrudRepository<Device> {

    List<Device> findTop10ByNameContainingIgnoreCase(String name);

}
