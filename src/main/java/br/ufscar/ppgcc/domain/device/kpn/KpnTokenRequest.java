package br.ufscar.ppgcc.domain.device.kpn;

import com.fasterxml.jackson.annotation.JsonProperty;

record KpnTokenRequest(@JsonProperty("grant_type") String grantType,
                       @JsonProperty("audience") String applicationId,
                       @JsonProperty("client_id") String clientId,
                       @JsonProperty("client_secret") String clientSecret) {

    KpnTokenRequest(String applicationId, String clientId, String clientSecret) {
        this("client_credentials", applicationId, clientId, clientSecret);
    }

}
