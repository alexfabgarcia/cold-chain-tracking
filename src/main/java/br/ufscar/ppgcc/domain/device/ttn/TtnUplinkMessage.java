package br.ufscar.ppgcc.domain.device.ttn;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.time.ZonedDateTime;
import java.util.Optional;

public record TtnUplinkMessage(@JsonProperty("end_device_ids") EndDevicesIds endDevicesIds,
                               @JsonProperty("uplink_message") UplinkMessage uplinkMessage,
                               @JsonProperty("received_at") ZonedDateTime receivedAt) {

    public record EndDevicesIds(@JsonProperty("device_id") String deviceId, @JsonProperty("dev_eui") String deviceEui) {}
    public record UplinkMessage(@JsonProperty("frm_payload") String base64Payload) {}

    public String deviceId() {
        return endDevicesIds().deviceId();
    }

    public String deviceEui() {
        return endDevicesIds().deviceEui();
    }

    public Optional<String> decodedPayload() {
        return Optional.ofNullable(uplinkMessage().base64Payload()).
                map(Base64::decodeBase64)
                .map(Hex::encodeHexString);
    }

}
