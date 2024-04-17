package br.ufscar.ppgcc.domain.device;

public interface NetworkEndDevice {

    String id();

    String name();

    NetworkServer networkServer();

    String eui();

}
