package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.Device;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends GridCrudRepository<Device> {

    List<Device> findTop10ByNameContainingIgnoreCase(String name);

    Optional<Device> findByExternalIdAndNetworkServer(String deviceExternalId, NetworkServer networkServer);

    Optional<Device> findByName(String name);

}
