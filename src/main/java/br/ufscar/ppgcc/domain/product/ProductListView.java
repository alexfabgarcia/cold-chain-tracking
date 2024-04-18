package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.common.views.MainLayout;
import br.ufscar.ppgcc.data.Product;
import br.ufscar.ppgcc.data.ProductMeasurementType;
import br.ufscar.ppgcc.domain.measurement.MeasurementTypeMultiSelect;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@RolesAllowed("ADMIN")
@PageTitle("Products")
@Route(value = "products", layout = MainLayout.class)
public class ProductListView extends CrudListView<Product, ProductDataProvider> {

    private final MeasurementTypeMultiSelect measurementMultiSelect;
    private GridPro<ProductMeasurementType> measurementTypeGrid;

    public ProductListView(ProductDataProvider dataProvider, MeasurementTypeMultiSelect measurementMultiSelect) {
        this.measurementMultiSelect = measurementMultiSelect;
        initCrud(dataProvider, Product.class);
    }

    @Override
    protected List<String> visibleColumns() {
        return List.of("name", "category");
    }

    @Override
    protected void setupSaveListener(CrudDataProvider<Product> dataProvider) {
        crud.addSaveListener(saveEvent -> {
            saveEvent.getItem().setMeasurementTypes(measurementTypeGrid.getListDataView().getItems().collect(Collectors.toSet()));
            dataProvider.save(saveEvent.getItem());
        });
    }

    @Override
    protected CrudEditor<Product> createEditor(ProductDataProvider dataProvider) {
        measurementTypeGrid = createMeasurementGrid();

        var name = new TextField("Name");
        var category = new Select<Product.Category>();
        category.setLabel("Category");
        category.setItems(Product.Category.values());

        measurementMultiSelect.addValueChangeListener(event -> {
            var productMeeasurementMap = measurementTypeGrid.getListDataView().getItems()
                    .collect(toMap(ProductMeasurementType::getMeasurementType, Function.identity()));

            var productMeasurementTypes = event.getValue().stream()
                    .map(measurementType -> productMeeasurementMap.getOrDefault(measurementType, new ProductMeasurementType(measurementType)))
                    .toList();

            measurementTypeGrid.setItems(productMeasurementTypes);
        });

        var binder = new Binder<>(Product.class);
        binder.forField(name).asRequired().bind(Product::getName, Product::setName);
        binder.forField(category).asRequired().bind(Product::getCategory, Product::setCategory);

        crud.addEditListener(event -> {
            var product = event.getItem();
            var measurementTypes = product.getMeasurementTypes();
            measurementMultiSelect.setValue(measurementTypes.stream()
                    .map(ProductMeasurementType::getMeasurementType).collect(Collectors.toSet()));
            measurementTypeGrid.setItems(measurementTypes);
        });

        var form = new FormLayout(name, category, measurementMultiSelect, measurementTypeGrid);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep(LumoUtility.MinWidth.NONE, 1),
                new FormLayout.ResponsiveStep(LumoUtility.MaxWidth.SCREEN_MEDIUM, 2));
        form.setColspan(measurementMultiSelect, 2);
        form.setColspan(measurementTypeGrid, 2);

        return new BinderCrudEditor<>(binder, form);
    }

    private GridPro<ProductMeasurementType> createMeasurementGrid() {
        measurementTypeGrid = new GridPro<>();
        measurementTypeGrid.setAllRowsVisible(true);
        measurementTypeGrid.addColumn(ProductMeasurementType::getMeasurementTypeWithUnit).setHeader("Measurement Type");
        measurementTypeGrid.addEditColumn(ProductMeasurementType::getMinimum).text(ProductMeasurementType::setMinimumSafely).setHeader("Minimum");
        measurementTypeGrid.addEditColumn(ProductMeasurementType::getMaximum).text(ProductMeasurementType::setMaximumSafely).setHeader("Maximum");
        return measurementTypeGrid;
    }

}
