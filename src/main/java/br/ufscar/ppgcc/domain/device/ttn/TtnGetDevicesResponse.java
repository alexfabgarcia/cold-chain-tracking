package br.ufscar.ppgcc.domain.device.ttn;

import br.ufscar.ppgcc.domain.device.NetworkEndDevice;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TtnGetDevicesResponse(@JsonProperty("end_devices") List<EndDevice> endDevices) {

    public record EndDevice(Ids ids, String name) implements NetworkEndDevice {

        @Override
        public String id() {
            return ids().id();
        }

        @Override
        public NetworkServer networkServer() {
            return NetworkServer.TTN;
        }

        @Override
        public String eui() {
            return ids().eui();
        }

        public record Ids(@JsonProperty("device_id") String id, @JsonProperty("dev_eui") String eui) {}

    }

}
