package br.ufscar.ppgcc.domain.measurement;

import br.ufscar.ppgcc.data.MeasurementType;
import br.ufscar.ppgcc.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Measurement Types")
@Route(value = "measurement-types", layout = MainLayout.class)
public class MeasurementTypeListView extends VerticalLayout {

    public MeasurementTypeListView(MeasurementTypeRepository repository) {
        final var grid = new Grid<>(MeasurementType.class);
        grid.addClassNames("measurement-types-grid");
        grid.setColumns("name", "measurementUnit");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setItems(repository.findAllByOrderByName());

        add(grid);
        setSizeFull();
    }

}
