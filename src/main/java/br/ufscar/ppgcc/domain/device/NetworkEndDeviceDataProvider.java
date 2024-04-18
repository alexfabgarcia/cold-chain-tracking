package br.ufscar.ppgcc.domain.device;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
class NetworkEndDeviceDataProvider extends AbstractBackEndDataProvider<NetworkEndDevice, Void> {

    private final transient NetworkEndDeviceService networkEndDeviceService;

    public NetworkEndDeviceDataProvider(NetworkEndDeviceService networkEndDeviceService) {
        this.networkEndDeviceService = networkEndDeviceService;
    }

    @Override
    protected Stream<NetworkEndDevice> fetchFromBackEnd(Query<NetworkEndDevice, Void> query) {
        return listEndDevices().stream();
    }

    @Override
    protected int sizeInBackEnd(Query<NetworkEndDevice, Void> query) {
        return listEndDevices().size();
    }

    public List<NetworkEndDevice> listEndDevices(NetworkServer networkServer) {
        return networkEndDeviceService.listDevices(networkServer);
    }

    public List<NetworkEndDevice> listEndDevices() {
        return networkEndDeviceService.listDevices();
    }

}
