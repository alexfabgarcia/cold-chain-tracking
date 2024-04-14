package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.data.Freight;
import br.ufscar.ppgcc.domain.device.DeviceComboBox;
import br.ufscar.ppgcc.domain.geolocation.OriginDestinationMap;
import br.ufscar.ppgcc.domain.product.ProductComboBox;
import br.ufscar.ppgcc.common.views.MainLayout;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@PageTitle("Freights")
@Route(value = "freights", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class FreightListView extends CrudListView<Freight, FreightDataProvider> {

    private final OriginDestinationMap originDestinationMap;
    private final ProductComboBox productComboBox;
    private final DeviceComboBox deviceComboBox;

    protected FreightListView(FreightDataProvider dataProvider, OriginDestinationMap originDestinationMap,
                              ProductComboBox productComboBox, DeviceComboBox deviceComboBox) {
        this.originDestinationMap = originDestinationMap;
        this.productComboBox = productComboBox;
        this.deviceComboBox = deviceComboBox;
        initCrud(dataProvider, Freight.class);
    }

    @Override
    protected List<String> visibleColumns() {
        return List.of("status", "originAddress", "destinationAddress", "productName", "deviceName", "carrierName");
    }

    @Override
    protected CrudEditor<Freight> createEditor(FreightDataProvider dataProvider) {
        var form = new FormLayout(originDestinationMap.getOrigin(), originDestinationMap.getDestination(),
                originDestinationMap.getMap(), productComboBox, deviceComboBox);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep(LumoUtility.MinWidth.NONE, 1),
                new FormLayout.ResponsiveStep(LumoUtility.MaxWidth.SCREEN_MEDIUM, 2));
        form.setColspan(originDestinationMap.getMap(), 2);

        var binder = new Binder<>(Freight.class);
        binder.forField(originDestinationMap.getOrigin()).asRequired().bind(Freight::getOrigin, Freight::setOrigin);
        binder.forField(originDestinationMap.getDestination()).asRequired().bind(Freight::getDestination, Freight::setDestination);
        binder.forField(productComboBox).asRequired().bind(Freight::getProduct, Freight::setProduct);
        binder.forField(deviceComboBox).asRequired().bind(Freight::getDevice, Freight::setDevice);

        return new BinderCrudEditor<>(binder, form);
    }

}
