package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.data.Device;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class DeviceDataProvider extends CrudDataProvider<Device> {

    private final transient DeviceRepository repository;

    public DeviceDataProvider(DeviceRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public List<NetworkServer> networkServers() {
        return List.of(NetworkServer.values());
    }

    public List<Device> search(String searchText) {
        return repository.findTop10ByNameContainingIgnoreCase(searchText);
    }
}
