package br.ufscar.ppgcc.domain.device.ttn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "ttnClient", url = "${ttn.stack.url}")
interface TtnClient {

    @GetMapping("/api/v3/applications/${ttn.stack.application-id}/devices?field_mask=name")
    TtnGetDevicesResponse listDevices(@RequestHeader("Authorization") String bearerToken);

    @PostMapping("/api/v3/as/applications/${ttn.stack.application-id}/devices/{deviceId}/down/push")
    void sendInstruction(@RequestHeader("Authorization") String bearerToken, @PathVariable String deviceId,
                         @RequestBody TtnDownlinkMessage body);

}
