package br.ufscar.ppgcc.domain.device.kpn;

import br.ufscar.ppgcc.domain.device.NetworkServer;
import br.ufscar.ppgcc.domain.device.NetworkProviderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class KpnNetworkProvider implements NetworkProviderService<KpnGetDevicesResponse.KpnDevice> {

    private final KpnGripClient kpnGripClient;
    private final KpnThingsClient kpnThingsClient;
    private final KpnTokenRequest kpnTokenRequest;

    KpnNetworkProvider(KpnGripClient kpnGripClient, KpnThingsClient kpnThingsClient, KpnTokenRequest kpnTokenRequest) {
        this.kpnGripClient = kpnGripClient;
        this.kpnThingsClient = kpnThingsClient;
        this.kpnTokenRequest = kpnTokenRequest;
    }

    @Override
    public NetworkServer name() {
        return NetworkServer.KPN;
    }

    @Override
    public List<KpnGetDevicesResponse.KpnDevice> listDevices() {
        var tokenResponse = kpnGripClient.getToken(kpnTokenRequest);
        var devicesResponse = kpnThingsClient.listDevices(String.format("Bearer %s", tokenResponse.token()));
        return devicesResponse.content();
    }

}
