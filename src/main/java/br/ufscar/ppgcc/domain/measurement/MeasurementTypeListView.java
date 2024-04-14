package br.ufscar.ppgcc.domain.measurement;

import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.common.views.MainLayout;
import br.ufscar.ppgcc.data.MeasurementType;
import br.ufscar.ppgcc.data.MeasurementUnit;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@PageTitle("Measurement Types")
@Route(value = "measurement-types", layout = MainLayout.class)
public class MeasurementTypeListView extends CrudListView<MeasurementType, MeasurementDataProvider> {

    public MeasurementTypeListView(MeasurementDataProvider dataProvider) {
        super(dataProvider, MeasurementType.class);
    }

    @Override
    protected List<String> visibleColumns() {
        return List.of("name", "measurementUnit");
    }

    @Override
    protected CrudEditor<MeasurementType> createEditor(MeasurementDataProvider dataProvider) {
        var name = new TextField("Name");
        var unit = new Select<MeasurementUnit>();
        unit.setLabel("Unit");
        unit.setItemLabelGenerator(MeasurementUnit::getDescription);
        unit.setItems(MeasurementUnit.values());

        var binder = new Binder<>(MeasurementType.class);
        binder.forField(name).asRequired().bind(MeasurementType::getName, MeasurementType::setName);
        binder.forField(unit).asRequired().bind(MeasurementType::getUnit, MeasurementType::setUnit);

        var form = new FormLayout(name, unit);

        return new BinderCrudEditor<>(binder, form);
    }
}
