package br.ufscar.ppgcc.domain.sensor;

import br.ufscar.ppgcc.data.SensorType;
import br.ufscar.ppgcc.common.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Sensor Types")
@Route(value = "sensor-types", layout = MainLayout.class)
public class SensorTypeListView extends VerticalLayout {

    public SensorTypeListView(SensorTypeRepository repository) {
        final var grid = new Grid<>(SensorType.class);
        grid.addClassNames("sensor-types-grid");
        grid.setColumns("name", "measurementUnit");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setItems(repository.findAllByOrderByName());

        add(grid);
        setSizeFull();
    }

}
