package br.ufscar.ppgcc.domain.device.ttn;

import br.ufscar.ppgcc.domain.device.Device;
import br.ufscar.ppgcc.domain.device.NetworkServer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

record TtnGetDevicesResponse(@JsonProperty("end_devices") List<EndDevice> endDevices) {

    record EndDevice(Ids ids, String name) implements Device {

        @Override
        public String id() {
            return ids().id();
        }

        @Override
        public NetworkServer networkServer() {
            return NetworkServer.TTN;
        }

        record Ids(@JsonProperty("device_id") String id) {}

    }

}
