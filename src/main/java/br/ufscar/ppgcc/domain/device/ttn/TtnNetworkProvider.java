package br.ufscar.ppgcc.domain.device.ttn;

import br.ufscar.ppgcc.data.ConditionViolatedEvent;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import br.ufscar.ppgcc.domain.device.NetworkProviderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class TtnNetworkProvider implements NetworkProviderService<TtnGetDevicesResponse.EndDevice> {

    private final TtnClient ttnClient;
    private final String ttnToken;

    TtnNetworkProvider(TtnClient ttnClient, @Value("${ttn.stack.token}") String ttnToken) {
        this.ttnClient = ttnClient;
        this.ttnToken = ttnToken;
    }

    @Override
    public NetworkServer name() {
        return NetworkServer.TTN;
    }

    @Override
    public List<TtnGetDevicesResponse.EndDevice> listDevices() {
        var response = ttnClient.listDevices(String.format("Bearer %s", ttnToken));
        return response.endDevices();
    }

    @Override
    public void notifyViolation(ConditionViolatedEvent event) {
        // TODO ttn downlink
    }

}
