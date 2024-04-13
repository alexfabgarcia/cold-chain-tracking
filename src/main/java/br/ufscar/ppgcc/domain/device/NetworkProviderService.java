package br.ufscar.ppgcc.domain.device;

import java.util.List;

public interface NetworkProviderService<T extends NetworkEndDevice> {

    NetworkServer name();

    List<T> listDevices();

}
