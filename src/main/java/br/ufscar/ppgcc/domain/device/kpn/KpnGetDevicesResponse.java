package br.ufscar.ppgcc.domain.device.kpn;

import br.ufscar.ppgcc.domain.device.NetworkEndDevice;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record KpnGetDevicesResponse(List<KpnDevice> content) {

    public record KpnDevice(@JsonProperty("uuid") String id, String name, String eui) implements NetworkEndDevice {

        @Override
        public NetworkServer networkServer() {
            return NetworkServer.KPN;
        }

    }

}
