package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@PageTitle("Devices")
@Route(value = "devices", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class DeviceListView extends VerticalLayout {

    private final Grid<Device> grid = new Grid<>(Device.class);

    public DeviceListView(DeviceService deviceService) {
        configureGrid(deviceService);
        add(createSelect(deviceService), grid);
        setSizeFull();
    }

    private Select<String> createSelect(DeviceService deviceService) {
        var select = new Select<String>();
        select.addClassName("network-provider-select");
        select.setLabel("Network Server");
        select.setEmptySelectionAllowed(true);
        select.setEmptySelectionCaption("All");
        select.setItems(Arrays.stream(NetworkServer.values()).map(Objects::toString).toList());
        select.addValueChangeListener(
                event -> Optional.ofNullable(event.getValue())
                        .map(NetworkServer::valueOf)
                        .ifPresentOrElse(
                                deviceManager -> grid.setItems(deviceService.listDevices(deviceManager)),
                                () -> grid.setItems(deviceService.listDevices())
                        )
        );
        return select;
    }

    private Grid<Device> configureGrid(DeviceService deviceService) {
        grid.addClassNames("devices-grid");
        grid.addColumn(Device::id).setHeader("ID");
        grid.addColumn(Device::name).setHeader("Name");
        grid.addColumn(Device::networkServer).setHeader("Network Server");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setItems(deviceService.listDevices());
        return grid;
    }

}
