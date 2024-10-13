package br.ufscar.ppgcc.domain.device.kpn;

import br.ufscar.ppgcc.data.ConditionViolatedEvent;
import br.ufscar.ppgcc.domain.device.NetworkProviderService;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class KpnNetworkProvider implements NetworkProviderService<KpnGetDevicesResponse.KpnDevice> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpnNetworkProvider.class);

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

    @Observed(name = "condition.violated", contextualName = "condition-violated",
            lowCardinalityKeyValues = {"networkServer", "kpn"})
    @Override
    public void notifyViolation(ConditionViolatedEvent event) {
        var payloadHex = KpnSenML.payloadFrom(event.getDevice().getEui(), event.getConditionsHex());
        if (event.getDevice().getName().startsWith("LoadTest KPN")) {
            LOGGER.info("Ignoring violation of load test device {}", event.getDevice().getName());
        } else {
            kpnThingsClient.sendInstruction(getBearerToken(), List.of(payloadHex));
        }
    }

}
