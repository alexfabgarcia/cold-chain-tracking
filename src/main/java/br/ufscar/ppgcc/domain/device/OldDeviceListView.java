package br.ufscar.ppgcc.domain.device;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class OldDeviceListView extends VerticalLayout {

    private final Grid<NetworkEndDevice> grid = new Grid<>(NetworkEndDevice.class);

    public OldDeviceListView(NetworkEndDeviceService networkEndDeviceService) {
        configureGrid(networkEndDeviceService);
        add(createSelect(networkEndDeviceService), grid);
        setSizeFull();
    }

    private Select<String> createSelect(NetworkEndDeviceService networkEndDeviceService) {
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
                                deviceManager -> grid.setItems(networkEndDeviceService.listDevices(deviceManager)),
                                () -> grid.setItems(networkEndDeviceService.listDevices())
                        )
        );
        return select;
    }

    private Grid<NetworkEndDevice> configureGrid(NetworkEndDeviceService networkEndDeviceService) {
        grid.addClassNames("devices-grid");
        grid.addColumn(NetworkEndDevice::id).setHeader("ID");
        grid.addColumn(NetworkEndDevice::name).setHeader("Name");
        grid.addColumn(NetworkEndDevice::networkServer).setHeader("Network Server");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setItems(networkEndDeviceService.listDevices());
        return grid;
    }

}
