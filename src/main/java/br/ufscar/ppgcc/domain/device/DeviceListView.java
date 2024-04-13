package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.data.Device;
import br.ufscar.ppgcc.domain.device.kpn.KpnGetDevicesResponse;
import br.ufscar.ppgcc.domain.device.ttn.TtnGetDevicesResponse;
import br.ufscar.ppgcc.views.MainLayout;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.List;
import java.util.Optional;

@PageTitle("Devices")
@Route(value = "devices", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class DeviceListView extends CrudListView<Device, DeviceDataProvider> {

    public DeviceListView(DeviceDataProvider dataProvider) {
        super(dataProvider, Device.class);
    }

    @Override
    protected List<String> visibleColumns() {
        return List.of("externalId", "name", "networkServer", "payloadPattern");
    }

    @Override
    protected CrudEditor<Device> createEditor(DeviceDataProvider dataProvider) {
        var deviceSelect = new Select<NetworkEndDevice>();
        deviceSelect.setLabel("End device");
        deviceSelect.setHelperText("Select a network server and then a device");
        deviceSelect.setItemLabelGenerator(endDevice -> String.format("(%s) %s - %s", endDevice.networkServer(), endDevice.name(), endDevice.id()));
        deviceSelect.setItems(dataProvider.listEndDevices());

        var networkServerSelect = new Select<NetworkServer>();
        networkServerSelect.setLabel("Network server");
        networkServerSelect.setItems(dataProvider.networkServers());
        networkServerSelect.setEmptySelectionAllowed(true);
        networkServerSelect.setEmptySelectionCaption("All");
        networkServerSelect.addValueChangeListener(event -> {
            var endDevices = Optional.ofNullable(event.getValue())
                    .map(dataProvider::listEndDevices)
                    .orElseGet(dataProvider::listEndDevices);
            deviceSelect.setItems(endDevices);
        });

        var payloadPattern = new TextField("Payload format");

        var binder = new Binder<>(Device.class);
        binder.forField(networkServerSelect).bind(Device::getNetworkServer, Device::setNetworkServer);
        binder.forField(deviceSelect).asRequired().bind(this::toNetworkEndDevice, Device::setFrom);
        binder.forField(payloadPattern).asRequired().bind(Device::getPayloadPattern, Device::setPayloadPattern);

        var form = new FormLayout(networkServerSelect, deviceSelect, payloadPattern);

        return new BinderCrudEditor<>(binder, form);
    }

    // FIXME Temporary for select binding
    private NetworkEndDevice toNetworkEndDevice(Device device) {
        if (device != null) {
            if (device.getNetworkServer() == NetworkServer.TTN) {
                return new TtnGetDevicesResponse.EndDevice(new TtnGetDevicesResponse.EndDevice.Ids(device.getExternalId()), device.getName());
            }
            return new KpnGetDevicesResponse.KpnDevice(device.getExternalId(), device.getName());
        }
        return null;
    }
}
