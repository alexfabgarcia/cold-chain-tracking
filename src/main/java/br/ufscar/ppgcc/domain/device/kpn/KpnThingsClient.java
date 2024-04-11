package br.ufscar.ppgcc.domain.device.kpn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "kpnThingsClient", url = "${kpn.things.url}")
interface KpnThingsClient {

    @GetMapping("/api/devices")
    KpnGetDevicesResponse listDevices(@RequestHeader("Authorization") String bearerToken);

}
