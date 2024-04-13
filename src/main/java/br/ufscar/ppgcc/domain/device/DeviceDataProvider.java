package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.common.GridCrudRepository;
import br.ufscar.ppgcc.data.Device;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class DeviceDataProvider extends CrudDataProvider<Device> {

    private final NetworkEndDeviceService networkEndDeviceService;

    public DeviceDataProvider(GridCrudRepository<Device> repository, NetworkEndDeviceService networkEndDeviceService) {
        super(repository);
        this.networkEndDeviceService = networkEndDeviceService;
    }

    public List<NetworkServer> networkServers() {
        return List.of(NetworkServer.values());
    }

    public List<NetworkEndDevice> listEndDevices(NetworkServer networkServer) {
        return networkEndDeviceService.listDevices(networkServer);
    }

    public List<NetworkEndDevice> listEndDevices() {
        return networkEndDeviceService.listDevices();
    }

}
