package br.ufscar.ppgcc.domain.carrier;

import br.ufscar.ppgcc.common.component.AutocompleteComboBox;
import br.ufscar.ppgcc.data.Carrier;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class CarrierComboBox extends AutocompleteComboBox<Carrier> {

    public CarrierComboBox(CarrierDataProvider dataProvider) {
        super(dataProvider::search);
        super.setLabel("Carrier");
        setItemLabelGenerator(Carrier::getFullName);
    }

    @Override
    protected int getMinCharacters() {
        return 3;
    }

}
