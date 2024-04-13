package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.data.Product;
import br.ufscar.ppgcc.data.ProductSensorType;
import br.ufscar.ppgcc.data.SensorType;
import br.ufscar.ppgcc.views.MainLayout;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@PageTitle("Products")
@Route(value = "products", layout = MainLayout.class)
public class ProductListView extends CrudListView<Product, ProductDataProvider> {

    private GridPro<ProductSensorType> sensorGrid;

    public ProductListView(ProductDataProvider dataProvider) {
        super(dataProvider, Product.class);
    }

    @Override
    protected List<String> visibleColumns() {
        return List.of("name", "category");
    }

    @Override
    protected CrudEditor<Product> createEditor(ProductDataProvider dataProvider) {
        sensorGrid = createSensorGrid();

        var name = new TextField("Name");
        var category = new Select<Product.Category>();
        category.setLabel("Category");
        category.setItems(Product.Category.values());
        var sensors = new MultiSelectComboBox<SensorType>("Sensor Type");
        sensors.setItems(dataProvider.sensorTypes());
        sensors.setItemLabelGenerator(SensorType::getName);
        sensors.addValueChangeListener(event -> {
            var productSensorMap = sensorGrid.getListDataView().getItems()
                    .collect(toMap(ProductSensorType::getSensorType, Function.identity()));

            var productSensorTypes = event.getValue().stream()
                    .map(sensorType -> productSensorMap.getOrDefault(sensorType, new ProductSensorType(sensorType)))
                    .toList();

            sensorGrid.setItems(productSensorTypes);
        });

        var binder = new Binder<>(Product.class);
        binder.forField(name).asRequired().bind(Product::getName, Product::setName);
        binder.forField(category).asRequired().bind(Product::getCategory, Product::setCategory);

        crud.addEditListener(event -> {
            var product = event.getItem();
            var sensorTypes = product.getSensorTypes();
            sensors.setValue(sensorTypes.stream()
                    .map(ProductSensorType::getSensorType).collect(Collectors.toSet()));
            sensorGrid.setItems(sensorTypes);
        });

        var form = new FormLayout(name, category, sensors, sensorGrid);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep(LumoUtility.MinWidth.NONE, 1),
                new FormLayout.ResponsiveStep(LumoUtility.MaxWidth.SCREEN_MEDIUM, 2));
        form.setColspan(sensors, 2);
        form.setColspan(sensorGrid, 2);

        return new BinderCrudEditor<>(binder, form);
    }

    private GridPro<ProductSensorType> createSensorGrid() {
        sensorGrid = new GridPro<>();
        sensorGrid.setAllRowsVisible(true);
        sensorGrid.addColumn(ProductSensorType::getSensorTypeWithUnit).setHeader("Sensor Type");
        sensorGrid.addEditColumn(ProductSensorType::getMinimum).text(ProductSensorType::setMinimumSafely).setHeader("Minimum");
        sensorGrid.addEditColumn(ProductSensorType::getMaximum).text(ProductSensorType::setMaximumSafely).setHeader("Maximum");
        return sensorGrid;
    }

    @Override
    protected void preSaveHook(Crud.SaveEvent<Product> saveEvent) {
        saveEvent.getItem().setSensorTypes(sensorGrid.getListDataView().getItems().collect(Collectors.toSet()));
    }
}
