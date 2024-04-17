package br.ufscar.ppgcc.data;

import org.apache.commons.codec.binary.Hex;
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

    public Device getDevice() {
        return device;
    }

}
