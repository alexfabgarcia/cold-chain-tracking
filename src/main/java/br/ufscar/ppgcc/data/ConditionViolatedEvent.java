package br.ufscar.ppgcc.data;

import org.apache.commons.codec.binary.Hex;
import org.eclipse.paho.client.mqttv3.internal.websocket.Base64;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class ConditionViolatedEvent extends ApplicationEvent {

    private final Device device;

    public ConditionViolatedEvent(List<String> conditions, Device device) {
        super(conditions);
        this.device = device;
    }

    private List<String> getConditions() {
        return (List<String>) getSource();
    }

    public String getConditionsHex() {
        var conditions = String.join(",", getConditions());
        return Hex.encodeHexString(conditions.getBytes());
    }

    public String getConditionsHexBase64Encoded() {
        return Base64.encode(getConditionsHex());
    }

    public Device getDevice() {
        return device;
    }

}
