package br.ufscar.ppgcc.domain.device.ttn;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

record TtnDownlinkMessage(@JsonProperty("downlinks")List<Downlink> downlinks) {

    record Downlink(@JsonProperty("f_port") int fPort, @JsonProperty("frm_payload") String payload) {
    }

    static TtnDownlinkMessage from(String payload) {
        return new TtnDownlinkMessage(List.of(new Downlink(1, payload)));
    }

}
