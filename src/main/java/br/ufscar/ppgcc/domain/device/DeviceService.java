package br.ufscar.ppgcc.domain.device;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final Map<NetworkServer, NetworkProviderService<? extends Device>> managers;

    public DeviceService(Collection<NetworkProviderService<? extends Device>> networkProviderServices) {
        this.managers = networkProviderServices.stream()
                .collect(Collectors.toMap(NetworkProviderService::name, Function.identity()));
    }

    public List<Device> listDevices() {
        return managers.values()
                .stream()
                .map(NetworkProviderService::name)
                .map(this::listDevices)
                .flatMap(Collection::stream)
                .toList();
    }

    @Cacheable(value = "kpn-devices", key = "networkServer")
    public <T extends Device> List<T> listDevices(NetworkServer networkServer) {

        // FIXME: It must be paged for production purposes
        return (List<T>) managers.get(networkServer).listDevices();
    }

}
