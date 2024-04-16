package br.ufscar.ppgcc.domain.freight;

import br.ufscar.ppgcc.data.Freight;
import br.ufscar.ppgcc.domain.device.DeviceComboBox;
import br.ufscar.ppgcc.domain.geolocation.GeolocationComboBox;
import br.ufscar.ppgcc.domain.product.ProductComboBox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FreightFormLayout extends FormLayout {

    private final ProductComboBox productComboBox;
    private final DeviceComboBox deviceComboBox;
    private final GeolocationComboBox origin;
    private final GeolocationComboBox destination;

    public FreightFormLayout(ProductComboBox productComboBox, DeviceComboBox deviceComboBox, GeolocationComboBox origin,
                             GeolocationComboBox destination) {
        this.productComboBox = productComboBox;
        this.deviceComboBox = deviceComboBox;
        this.origin = origin;
        this.destination = destination;
        this.origin.setLabel("Origin");
        this.destination.setLabel("Destination");

        add(productComboBox, deviceComboBox, origin, destination);
        setResponsiveSteps(new FormLayout.ResponsiveStep(LumoUtility.MinWidth.NONE, 1),
                new FormLayout.ResponsiveStep(LumoUtility.MaxWidth.SCREEN_MEDIUM, 2));
    }

    public void setReadOnly(boolean readOnly) {
        getChildren().filter(ComboBox.class::isInstance)
                .map(ComboBox.class::cast)
                .forEach(comboBox -> comboBox.setReadOnly(readOnly));
    }

    public void setReadOnly(Freight freight) {
        getOriginComboBox().setValue(freight.getOrigin());
        getDestinationComboBox().setValue(freight.getDestination());
        productComboBox.setValue(freight.getProduct());
        deviceComboBox.setValue(freight.getDevice());

        setReadOnly(true);
    }

    public GeolocationComboBox getOriginComboBox() {
        return origin;
    }

    public GeolocationComboBox getDestinationComboBox() {
        return destination;
    }

    public ProductComboBox getProductComboBox() {
        return productComboBox;
    }

    public DeviceComboBox getDeviceComboBox() {
        return deviceComboBox;
    }
}
