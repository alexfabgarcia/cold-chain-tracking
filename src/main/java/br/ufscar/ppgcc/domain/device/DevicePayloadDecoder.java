package br.ufscar.ppgcc.domain.device;

import java.util.Map;

public interface DevicePayloadDecoder {

    String id();

    String name();

    Map<String, Double> decode(String payloadHex);

}
