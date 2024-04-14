package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.data.Device;
import br.ufscar.ppgcc.data.SensorType;
import br.ufscar.ppgcc.domain.sensor.SensorTypeRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class DeviceDataProvider extends CrudDataProvider<Device> {

    private final DeviceRepository repository;
    private final NetworkEndDeviceService networkEndDeviceService;
    private final SensorTypeRepository sensorTypeRepository;

    public DeviceDataProvider(DeviceRepository repository, NetworkEndDeviceService networkEndDeviceService,
                              SensorTypeRepository sensorTypeRepository) {
        super(repository);
        this.repository = repository;
        this.networkEndDeviceService = networkEndDeviceService;
        this.sensorTypeRepository = sensorTypeRepository;
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

    public List<SensorType> sensorTypes() {
        return sensorTypeRepository.findAllByOrderByName();
    }

    public List<Device> search(String searchText) {
        return repository.findTop10ByNameContainingIgnoreCase(searchText);
    }
}
