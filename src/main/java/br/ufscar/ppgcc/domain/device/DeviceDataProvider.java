package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.data.Device;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class DeviceDataProvider extends CrudDataProvider<Device> {

    private final DeviceRepository repository;
    private final NetworkEndDeviceService networkEndDeviceService;

    public DeviceDataProvider(DeviceRepository repository, NetworkEndDeviceService networkEndDeviceService) {
        super(repository);
        this.repository = repository;
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

    public List<Device> search(String searchText) {
        return repository.findTop10ByNameContainingIgnoreCase(searchText);
    }
}
