package br.ufscar.ppgcc.domain.device.ttn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "ttnClient", url = "${ttn.stack.url}")
interface TtnClient {

    @GetMapping("/api/v3/applications/${ttn.stack.application-id}/devices?field_mask=name")
    TtnGetDevicesResponse listDevices(@RequestHeader("Authorization") String bearerToken);

}
