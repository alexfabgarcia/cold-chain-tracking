package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.common.CrudDataProvider;
import br.ufscar.ppgcc.common.CrudListView;
import br.ufscar.ppgcc.data.Product;
import br.ufscar.ppgcc.views.MainLayout;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@PageTitle("Products")
@Route(value = "products", layout = MainLayout.class)
public class ProductListView extends CrudListView<Product> {

    public ProductListView(CrudDataProvider<Product> dataProvider) {
        super(dataProvider, Product.class);
    }

    @Override
    protected CrudEditor<Product> createEditor() {
        var name = new TextField("Name");
        var category = new Select<Product.Category>();
        category.setLabel("Category");
        category.setItems(Product.Category.VACCINE);

        var binder = new Binder<>(Product.class);
        binder.forField(name).asRequired().bind(Product::getName, Product::setName);
        binder.forField(category).asRequired().bind(Product::getCategory, Product::setCategory);

        var form = new FormLayout(name, category);

        return new BinderCrudEditor<>(binder, form);
    }

    @Override
    protected List<String> visibleColumns() {
        return List.of("name", "category");
    }

}
