package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.data.Freight;
import br.ufscar.ppgcc.domain.device.DeviceComboBox;
import br.ufscar.ppgcc.domain.geolocation.GeolocationComboBox;
import br.ufscar.ppgcc.domain.product.ProductComboBox;
import br.ufscar.ppgcc.common.views.MainLayout;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@PageTitle("Freights")
@Route(value = "freights", layout = MainLayout.class)
public class FreightListView extends CrudListView<Freight, FreightDataProvider> {

    private final GeolocationComboBox originComboBox;
    private final GeolocationComboBox destinationComboBox;
    private final ProductComboBox productComboBox;
    private final DeviceComboBox deviceComboBox;

    protected FreightListView(FreightDataProvider dataProvider, GeolocationComboBox originComboBox,
                              GeolocationComboBox destinationComboBox, ProductComboBox productComboBox,
                              DeviceComboBox deviceComboBox) {
        this.originComboBox = originComboBox;
        this.destinationComboBox = destinationComboBox;
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
        originComboBox.setLabel("Origin");
        destinationComboBox.setLabel("Destination");
        var form = new FormLayout(originComboBox, destinationComboBox, productComboBox, deviceComboBox);

        var binder = new Binder<>(Freight.class);
        binder.forField(originComboBox).asRequired().bind(Freight::getOrigin, Freight::setOrigin);
        binder.forField(destinationComboBox).asRequired().bind(Freight::getDestination, Freight::setDestination);
        binder.forField(productComboBox).asRequired().bind(Freight::getProduct, Freight::setProduct);
        binder.forField(deviceComboBox).asRequired().bind(Freight::getDevice, Freight::setDevice);

        return new BinderCrudEditor<>(binder, form);
    }

}
