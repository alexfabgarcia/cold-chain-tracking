package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.common.views.MainLayout;
import br.ufscar.ppgcc.data.Freight;
import br.ufscar.ppgcc.domain.geolocation.OriginDestinationMap;
import br.ufscar.ppgcc.domain.security.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteParam;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.isNull;

@RolesAllowed({"ROLE_ADMIN", "ROLE_CARRIER"})
@PageTitle("Freights")
@Route(value = "freights", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class FreightListView extends CrudListView<Freight, FreightDataProvider> {

    private final FreightFormLayout formLayout;
    private final transient SecurityService securityService;
    private final transient FreightService freightService;

    private final Button startButton = new Button("Start");

    protected FreightListView(FreightDataProvider dataProvider, FreightFormLayout formLayout, SecurityService securityService, FreightService freightService) {
        this.formLayout = formLayout;
        this.securityService = securityService;
        this.freightService = freightService;
        initCrud(dataProvider, Freight.class);
        setupStartButton();
    }

    private void setupStartButton() {
        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        startButton.setEnabled(false);
        startButton.addClickListener(event -> crud.getGrid().getSelectedItems().stream().findAny().ifPresent(this::start));
    }

    private void start(Freight freight) {
        freightService.start(freight, securityService.getAuthenticatedUser().getUsername());
        crud.getDataProvider().refreshAll();
    }

    @Override
    protected List<String> visibleColumns() {
        return List.of("status", "originAddress", "destinationAddress", "productName", "deviceName", "carrierName");
    }

    @Override
    protected void initCrud(FreightDataProvider dataProvider, Class<Freight> beanType) {
        super.initCrud(dataProvider, beanType);
        crud.addEditListener(event -> {
            if (!event.getItem().isCreated()) {
                formLayout.setReadOnly(true);
            }
        });
    }

    @Override
    protected CrudEditor<Freight> createEditor(FreightDataProvider dataProvider) {
        var binder = new Binder<>(Freight.class);
        binder.forField(formLayout.getProductComboBox()).asRequired().bind(Freight::getProduct, Freight::setProduct);
        binder.forField(formLayout.getDeviceComboBox()).asRequired().bind(Freight::getDevice, Freight::setDevice);
        binder.forField(formLayout.getCarrierComboBox()).asRequired().bind(Freight::getCarrier, Freight::setCarrier);
        binder.forField(formLayout.getDescription()).bind(Freight::getDescription, Freight::setDescription);
        binder.forField(formLayout.getOriginComboBox()).asRequired().bind(Freight::getOrigin, Freight::setOrigin);
        binder.forField(formLayout.getDestinationComboBox()).asRequired().bind(Freight::getDestination, Freight::setDestination);

        var map = new OriginDestinationMap(formLayout.getOriginComboBox(), formLayout.getDestinationComboBox());
        formLayout.setReadOnly(false);
        formLayout.add(map);
        formLayout.setColspan(map, 2);

        return new BinderCrudEditor<>(binder, formLayout);
    }

    @Override
    protected void setupGrid() {
        super.setupGrid();
        var grid = crud.getGrid();
        new FreightContextMenu(grid, this::start);

        grid.addColumn(new ComponentRenderer<>(Button::new, (button, freight) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> button.getUI().ifPresent(ui -> measurementLink(ui, freight)));
            button.setIcon(LineAwesomeIcon.TEMPERATURE_HIGH_SOLID.create());
        })).setKey("measurements");

        if (securityService.isCarrier()) {
            configureCarrierView();
        }
    }

    private void measurementLink(UI ui, Freight freight) {
        ui.navigate(FreightMeasurementView.class, new RouteParam("id", freight.getId().toString()));
    }

    private void configureCarrierView() {
        formLayout.setReadOnly(true);
        formLayout.add(startButton);

        crud.setToolbar(startButton);
        crud.getNewButton().setVisible(false);
        crud.getDeleteButton().setEnabled(false);

        crud.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);
        crud.getGrid().addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(freight -> startButton.setEnabled(freight.isCreated() && securityService.isCarrier())));
    }

    private class FreightContextMenu extends GridContextMenu<Freight> {
        FreightContextMenu(Grid<Freight> target, Consumer<Freight> startConsumer) {
            super(target);
            addItem("Edit", e -> e.getItem().ifPresent(freight -> crud.edit(freight, Crud.EditMode.EXISTING_ITEM)));
            addItem("Measurements", e -> e.getItem().ifPresent(freight -> getUI().ifPresent(ui -> measurementLink(ui, freight))));
            var startLink = addItem("Start", e -> e.getItem().ifPresent(startConsumer));

            setDynamicContentHandler(freight -> {
                if (isNull(freight)) {
                    return false;
                }
                startLink.setVisible(freight.isCreated());
                return true;
            });
        }
    }
}
