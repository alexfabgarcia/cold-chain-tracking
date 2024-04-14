package br.ufscar.ppgcc.domain.measurement;

import br.ufscar.ppgcc.data.MeasurementType;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MeasurementTypeMultiSelect extends MultiSelectComboBox<MeasurementType> {

    public MeasurementTypeMultiSelect(MeasurementDataProvider measurementDataProvider) {
        setLabel("Measurement Type");
        setItems(measurementDataProvider.measurementTypes());
        setItemLabelGenerator(MeasurementType::getName);
    }

}
