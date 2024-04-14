package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.data.Freight;
import br.ufscar.ppgcc.domain.device.DeviceComboBox;
import br.ufscar.ppgcc.domain.geolocation.GeolocationComboBox;
import br.ufscar.ppgcc.domain.geolocation.OriginDestinationMap;
import br.ufscar.ppgcc.domain.product.ProductComboBox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.map.configuration.Feature;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FreightFormLayout extends FormLayout {

    private final OriginDestinationMap originDestinationMap;
    private final ProductComboBox productComboBox;
    private final DeviceComboBox deviceComboBox;

    public FreightFormLayout(ProductComboBox productComboBox, DeviceComboBox deviceComboBox,
                             OriginDestinationMap originDestinationMap) {
        this.originDestinationMap = originDestinationMap;
        this.productComboBox = productComboBox;
        this.deviceComboBox = deviceComboBox;

        add(productComboBox, deviceComboBox, originDestinationMap.getOrigin(), originDestinationMap.getDestination(),
                originDestinationMap.getMap());
        setResponsiveSteps(new FormLayout.ResponsiveStep(LumoUtility.MinWidth.NONE, 1),
                new FormLayout.ResponsiveStep(LumoUtility.MaxWidth.SCREEN_MEDIUM, 2));
        setColspan(originDestinationMap.getMap(), 2);
    }

    public void setReadOnly(Freight freight) {
        getOriginComboBox().setValue(freight.getOrigin());
        getDestinationComboBox().setValue(freight.getDestination());
        productComboBox.setValue(freight.getProduct());
        deviceComboBox.setValue(freight.getDevice());

        getChildren().filter(ComboBox.class::isInstance)
                .map(ComboBox.class::cast)
                .forEach(comboBox -> {
                    comboBox.setReadOnly(true);
                    comboBox.setHelperText(null);
                });
    }

    public void attachToMap(Feature feature) {
        originDestinationMap.getMap().attach(feature);
    }

    public GeolocationComboBox getOriginComboBox() {
        return originDestinationMap.getOrigin();
    }

    public GeolocationComboBox getDestinationComboBox() {
        return originDestinationMap.getDestination();
    }

    public ProductComboBox getProductComboBox() {
        return productComboBox;
    }

    public DeviceComboBox getDeviceComboBox() {
        return deviceComboBox;
    }
}
