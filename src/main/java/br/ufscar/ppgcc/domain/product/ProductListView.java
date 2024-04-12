package br.ufscar.ppgcc.domain.product;

import br.ufscar.ppgcc.data.Product;
import br.ufscar.ppgcc.views.MainLayout;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudGrid;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Products")
@Route(value = "products", layout = MainLayout.class)
public class ProductListView extends VerticalLayout {

    private final Crud<Product> crud;

    public ProductListView(ProductDataProvider productDataProvider) {
        var grid = new CrudGrid<>(Product.class, false);
        crud = new Crud<>(Product.class, grid, createEditor());
        grid.addItemDoubleClickListener(event -> crud.edit(event.getItem(), Crud.EditMode.EXISTING_ITEM));
        setupDataProvider(productDataProvider);
        add(crud);
        setSizeFull();
    }

    private CrudEditor<Product> createEditor() {
        var name = new TextField("Product name");
        var category = new Select<Product.Category>();
        category.setLabel("Category");
        category.setItems(Product.Category.VACCINE);

        var form = new FormLayout(name, category);

        var binder = new Binder<>(Product.class);
        binder.forField(name).asRequired().bind(Product::getName, Product::setName);
        binder.forField(category).asRequired().bind(Product::getCategory, Product::setCategory);

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupDataProvider(ProductDataProvider productDataProvider) {
        crud.setDataProvider(productDataProvider);
        crud.addDeleteListener(deleteEvent -> productDataProvider.delete(deleteEvent.getItem()));
        crud.addSaveListener(saveEvent -> productDataProvider.persist(saveEvent.getItem()));
    }
}
