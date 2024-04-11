package br.ufscar.ppgcc.domain.carrier;

import br.ufscar.ppgcc.data.Carrier;
import br.ufscar.ppgcc.domain.carrier.CarrierRepository;
import br.ufscar.ppgcc.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Carriers")
@Route(value = "carriers", layout = MainLayout.class)
public class CarrierListView extends VerticalLayout {

    public CarrierListView(CarrierRepository repository) {
        final var grid = new Grid<>(Carrier.class);
        grid.addClassNames("carriers-grid");
        grid.setColumns("firstName", "surname", "phone");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setItems(repository.findAllByOrderByFirstName());

        add(grid);
        setSizeFull();
    }

}
