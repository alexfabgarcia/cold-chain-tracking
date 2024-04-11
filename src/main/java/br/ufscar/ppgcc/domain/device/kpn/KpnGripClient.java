package br.ufscar.ppgcc.domain.device.kpn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "kpnGripClient", url = "${kpn.grip.url}")
interface KpnGripClient {

    @PostMapping("/v2/${kpn.grip.tenant-id}/oidc/idp/c1/token")
    KpnTokenResponse getToken(@RequestBody KpnTokenRequest kpnTokenRequest);

}
