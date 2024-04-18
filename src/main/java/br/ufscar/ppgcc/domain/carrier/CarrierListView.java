package br.ufscar.ppgcc.domain.carrier;

import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.data.Carrier;
import br.ufscar.ppgcc.common.views.MainLayout;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@RolesAllowed("ADMIN")
@PageTitle("Carriers")
@Route(value = "carriers", layout = MainLayout.class)
public class CarrierListView extends CrudListView<Carrier, CarrierDataProvider> {

    public CarrierListView(CarrierDataProvider dataProvider) {
        super(dataProvider, Carrier.class);
    }

    @Override
    protected List<String> visibleColumns() {
        return List.of("firstName", "surname", "phone");
    }

    @Override
    protected CrudEditor<Carrier> createEditor(CarrierDataProvider dataProvider) {
        var firstName = new TextField("First name");
        var surname = new TextField("Surname");
        var phone = new TextField("Phone");

        var binder = new Binder<>(Carrier.class);
        binder.forField(firstName).asRequired().bind(Carrier::getFirstName, Carrier::setFirstName);
        binder.forField(surname).asRequired().bind(Carrier::getSurname, Carrier::setSurname);
        binder.forField(phone).asRequired().bind(Carrier::getPhone, Carrier::setPhone);

        var form = new FormLayout(firstName, surname, phone);

        return new BinderCrudEditor<>(binder, form);
    }
}
