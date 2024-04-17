package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.data.ConditionViolatedEvent;

import java.util.List;

public interface NetworkProviderService<T extends NetworkEndDevice> {

    NetworkServer name();

    List<T> listDevices();

    void notifyViolation(ConditionViolatedEvent event);

}
