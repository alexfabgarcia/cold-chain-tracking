package br.ufscar.ppgcc.domain.carrier;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.data.Carrier;
import br.ufscar.ppgcc.views.MainLayout;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@PageTitle("Carriers")
@Route(value = "carriers", layout = MainLayout.class)
public class CarrierListView extends CrudListView<Carrier> {

    public CarrierListView(CrudDataProvider<Carrier> dataProvider) {
        super(dataProvider, Carrier.class);
    }

    @Override
    protected CrudEditor<Carrier> createEditor() {
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

    @Override
    protected List<String> visibleColumns() {
        return List.of("firstName", "surname", "phone");
    }
}
