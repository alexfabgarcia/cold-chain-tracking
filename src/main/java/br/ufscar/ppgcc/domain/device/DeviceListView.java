package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.common.views.MainLayout;
import br.ufscar.ppgcc.data.Device;
import br.ufscar.ppgcc.domain.device.kpn.KpnGetDevicesResponse;
import br.ufscar.ppgcc.domain.device.ttn.TtnGetDevicesResponse;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@RolesAllowed("ADMIN")
@PageTitle("Devices")
@Route(value = "devices", layout = MainLayout.class)
public class DeviceListView extends CrudListView<Device, DeviceDataProvider> {
    private final NetworkEndDeviceDataProvider networkEndDeviceDataProvider;
    private final transient Map<String, DevicePayloadDecoder> decoders;

    public DeviceListView(DeviceDataProvider dataProvider, NetworkEndDeviceDataProvider networkEndDeviceDataProvider,
                          List<DevicePayloadDecoder> decoders) {
        this.networkEndDeviceDataProvider = networkEndDeviceDataProvider;
        this.decoders = decoders.stream().collect(toMap(DevicePayloadDecoder::id, Function.identity()));
        initCrud(dataProvider, Device.class);
    }

    @Override
    protected List<String> visibleColumns() {
        return List.of("externalId", "name", "networkServer", "payloadDecoder");
    }

    @Override
    protected CrudEditor<Device> createEditor(DeviceDataProvider dataProvider) {
        var deviceSelect = new Select<NetworkEndDevice>();
        deviceSelect.setLabel("End device");
        deviceSelect.setHelperText("Select a network server and then a device");
        deviceSelect.setItemLabelGenerator(endDevice -> String.format("(%s) %s - %s", endDevice.networkServer(), endDevice.name(), endDevice.id()));
        deviceSelect.setItems(networkEndDeviceDataProvider);

        var networkServerSelect = new Select<NetworkServer>();
        networkServerSelect.setLabel("Network server");
        networkServerSelect.setItems(dataProvider.networkServers());
        networkServerSelect.setEmptySelectionAllowed(true);
        networkServerSelect.setEmptySelectionCaption("All");
        networkServerSelect.addValueChangeListener(event -> {
            var endDevices = Optional.ofNullable(event.getValue())
                    .map(networkEndDeviceDataProvider::listEndDevices)
                    .orElseGet(networkEndDeviceDataProvider::listEndDevices);
            deviceSelect.setItems(endDevices);
        });

        var payloadDecoder = new Select<DevicePayloadDecoder>();
        payloadDecoder.setLabel("Payload Decoder");
        payloadDecoder.setItems(decoders.values());
        payloadDecoder.setItemLabelGenerator(DevicePayloadDecoder::name);

        var binder = new Binder<>(Device.class);
        binder.forField(networkServerSelect).bind(Device::getNetworkServer, Device::setNetworkServer);
        binder.forField(deviceSelect).asRequired().bind(this::toNetworkEndDevice, Device::setFrom);
        binder.forField(payloadDecoder).asRequired().bind(device -> decoders.get(device.getPayloadDecoder()),
                (device, value) -> device.setPayloadDecoder(value.id()));

        var form = new FormLayout(networkServerSelect, deviceSelect, payloadDecoder);

        return new BinderCrudEditor<>(binder, form);
    }

    private NetworkEndDevice toNetworkEndDevice(Device device) {
        if (device != null) {
            if (device.getNetworkServer() == NetworkServer.TTN) {
                var endDeviceIds = new TtnGetDevicesResponse.EndDevice.Ids(device.getExternalId(), device.getEui());
                return new TtnGetDevicesResponse.EndDevice(endDeviceIds, device.getName());
            }
            return new KpnGetDevicesResponse.KpnDevice(device.getExternalId(), device.getName(), device.getEui());
        }
        return null;
    }
}
