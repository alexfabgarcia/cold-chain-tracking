package br.ufscar.ppgcc.domain.device.kpn;

import br.ufscar.ppgcc.domain.device.Device;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

record KpnGetDevicesResponse(List<KpnDevice> content) {
    record KpnDevice(@JsonProperty("uuid") String id, String name, String status) implements Device {
        @Override
        public NetworkServer networkServer() {
            return NetworkServer.KPN;
        }
    }
}
