package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.common.component.AutocompleteComboBox;
import br.ufscar.ppgcc.data.Product;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class ProductComboBox extends AutocompleteComboBox<Product> {

    public ProductComboBox(ProductDataProvider dataProvider) {
        super(dataProvider::search);
        super.setLabel("Product");
        setItemLabelGenerator(Product::getName);
    }
}
