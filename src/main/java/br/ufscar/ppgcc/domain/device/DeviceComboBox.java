package br.ufscar.ppgcc.domain.device;

import br.ufscar.ppgcc.common.component.AutocompleteComboBox;
import br.ufscar.ppgcc.data.Device;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class DeviceComboBox extends AutocompleteComboBox<Device> {

    public DeviceComboBox(DeviceDataProvider dataProvider) {
        super(dataProvider::search);
        super.setLabel("Device");
        setItemLabelGenerator(Device::getName);
    }
}
