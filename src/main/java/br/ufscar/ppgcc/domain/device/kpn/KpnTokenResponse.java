package br.ufscar.ppgcc.domain.device.kpn;

import com.fasterxml.jackson.annotation.JsonProperty;

record KpnTokenResponse(@JsonProperty("access_token") String token) {
}
