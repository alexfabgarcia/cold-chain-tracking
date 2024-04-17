package br.ufscar.ppgcc.domain.device.kpn;

import br.ufscar.ppgcc.data.ConditionViolatedEvent;
import br.ufscar.ppgcc.domain.device.NetworkProviderService;
import br.ufscar.ppgcc.domain.device.NetworkServer;
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
        var devicesResponse = kpnThingsClient.listDevices(getBearerToken());
        return devicesResponse.content();
    }

    private String getBearerToken() {
        var tokenResponse = kpnGripClient.getToken(kpnTokenRequest);
        return String.format("Bearer %s", tokenResponse.token());
    }

    @Override
    public void notifyViolation(ConditionViolatedEvent event) {
        var payloadHex = KpnSenML.payloadFrom(event.getDevice().getEui(), event.getConditionsHex());
        kpnThingsClient.sendInstruction(getBearerToken(), List.of(payloadHex));
    }

}
