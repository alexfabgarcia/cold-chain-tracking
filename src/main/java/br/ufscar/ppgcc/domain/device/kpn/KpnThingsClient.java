package br.ufscar.ppgcc.domain.device.kpn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(value = "kpnThingsClient", url = "${kpn.things.url}")
interface KpnThingsClient {

    @GetMapping("/api/devices")
    KpnGetDevicesResponse listDevices(@RequestHeader("Authorization") String bearerToken);

    @PostMapping("/api/actuator/downlinks?port=1")
    void sendInstruction(@RequestHeader("Authorization") String bearerToken, List<KpnSenML> payload);

}
