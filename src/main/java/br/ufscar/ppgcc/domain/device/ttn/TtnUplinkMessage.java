package br.ufscar.ppgcc.domain.device.ttn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helger.commons.base64.Base64;
import org.springframework.messaging.converter.MessageConversionException;

import java.io.IOException;
import java.time.ZonedDateTime;

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

    public String decodedPayload() {
        try {
            return new String(Base64.decode(uplinkMessage().base64Payload()));
        } catch (IOException e) {
            throw new MessageConversionException("Invalid Base64 payload.", e);
        }
    }

}
