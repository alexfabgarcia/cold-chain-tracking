package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.common.views.MainLayout;
import br.ufscar.ppgcc.data.Freight;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteParam;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;

@PageTitle("Freights")
@Route(value = "freights", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class FreightListView extends CrudListView<Freight, FreightDataProvider> {

    private final FreightFormLayout formLayout;

    protected FreightListView(FreightDataProvider dataProvider, FreightFormLayout formLayout) {
        this.formLayout = formLayout;
        initCrud(dataProvider, Freight.class);
    }

    @Override
    protected List<String> visibleColumns() {
        return List.of("status", "originAddress", "destinationAddress", "productName", "deviceName", "carrierName");
    }

    @Override
    protected CrudEditor<Freight> createEditor(FreightDataProvider dataProvider) {
        var binder = new Binder<>(Freight.class);
        binder.forField(formLayout.getProductComboBox()).asRequired().bind(Freight::getProduct, Freight::setProduct);
        binder.forField(formLayout.getDeviceComboBox()).asRequired().bind(Freight::getDevice, Freight::setDevice);
        binder.forField(formLayout.getOriginComboBox()).asRequired().bind(Freight::getOrigin, Freight::setOrigin);
        binder.forField(formLayout.getDestinationComboBox()).asRequired().bind(Freight::getDestination, Freight::setDestination);

        return new BinderCrudEditor<>(binder, formLayout);
    }

    @Override
    protected void setupGrid() {
        super.setupGrid();
        crud.getGrid().addColumn(new ComponentRenderer<>(Button::new, (button, freight) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> button.getUI().ifPresent(ui ->
                    ui.navigate(FreightMeasurementView.class, new RouteParam("id", freight.getId().toString()))));
            button.setIcon(LineAwesomeIcon.TEMPERATURE_HIGH_SOLID.create());
        })).setKey("measurements");
    }

}
