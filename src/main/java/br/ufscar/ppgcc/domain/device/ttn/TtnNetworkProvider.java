package br.ufscar.ppgcc.domain.device.ttn;

import br.ufscar.ppgcc.data.ConditionViolatedEvent;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import br.ufscar.ppgcc.domain.device.NetworkProviderService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class TtnNetworkProvider implements NetworkProviderService<TtnGetDevicesResponse.EndDevice> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TtnNetworkProvider.class);

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
        var response = ttnClient.listDevices(bearerToken());
        return response.endDevices();
    }

    private String bearerToken() {
        return String.format("Bearer %s", ttnToken);
    }

    @Override
    @Observed(name = "condition.violated", contextualName = "condition-violated",
            lowCardinalityKeyValues = {"networkServer", "ttn"})
    public void notifyViolation(ConditionViolatedEvent event) {
        var downlinkMessage = TtnDownlinkMessage.from(event.getConditionsHexBase64Encoded());
        if (event.getDevice().getName().startsWith("LoadTest TTN")) {
            LOGGER.info("Ignoring violation of load test device {}", event.getDevice().getName());
        } else {
            ttnClient.sendInstruction(bearerToken(), event.getDevice().getExternalId(), downlinkMessage);
        }
    }

}
