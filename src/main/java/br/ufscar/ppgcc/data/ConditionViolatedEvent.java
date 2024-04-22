package br.ufscar.ppgcc.data;

import org.apache.commons.codec.binary.Hex;
import org.eclipse.paho.client.mqttv3.internal.websocket.Base64;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class ConditionViolatedEvent extends ApplicationEvent {

    private final Freight freight;

    public ConditionViolatedEvent(List<FreightViolation> conditions, Freight freight) {
        super(conditions);
        this.freight = freight;
    }

    private List<FreightViolation> getConditions() {
        return (List<FreightViolation>) getSource();
    }

    public String getConditionsHex() {
        var conditions = String.join(",", getConditions().stream().map(FreightViolation::name).toList());
        return Hex.encodeHexString(conditions.getBytes());
    }

    public String getConditionsHexBase64Encoded() {
        return Base64.encode(getConditionsHex());
    }

    public Device getDevice() {
        return freight.getDevice();
    }

}
