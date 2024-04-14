package br.ufscar.ppgcc.common;

import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudGrid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class CrudListView<T, P extends CrudDataProvider<T>> extends VerticalLayout {

    private static final String EDIT_COLUMN = "vaadin-crud-edit-column";

    protected final Crud<T> crud = new Crud<>();

    protected CrudListView() {
    }

    protected CrudListView(P dataProvider, Class<T> beanType) {
        initCrud(dataProvider, beanType);
    }

    protected abstract List<String> visibleColumns();

    protected abstract CrudEditor<T> createEditor(P dataProvider);

    protected void initCrud(P dataProvider, Class<T> beanType) {

        // Create grid before to disable filters
        var grid = new CrudGrid<>(beanType, false);

        crud.setBeanType(beanType);
        crud.setGrid(grid);
        crud.setEditor(createEditor(dataProvider));
        crud.setSizeFull();

        setupDataProvider(dataProvider);
        setupGrid();

        add(crud);
        setSizeFull();
    }

    private void setupDataProvider(CrudDataProvider<T> dataProvider) {
        crud.setDataProvider(dataProvider);
        setupSaveListener(dataProvider);
        setupDeleteListener(dataProvider);
    }

    protected void setupSaveListener(CrudDataProvider<T> dataProvider) {
        crud.addSaveListener(saveEvent -> dataProvider.save(saveEvent.getItem()));
    }

    protected void setupDeleteListener(CrudDataProvider<T> dataProvider) {
        crud.addDeleteListener(deleteEvent -> dataProvider.delete(deleteEvent.getItem()));
    }

    private void setupGrid() {
        var grid = crud.getGrid();
        grid.addItemDoubleClickListener(event -> crud.edit(event.getItem(), Crud.EditMode.EXISTING_ITEM));

        var columns = columns();
        grid.getColumns().forEach(column -> {
            String key = column.getKey();
            if (!columns.contains(key)) {
                grid.removeColumn(column);
            }
        });

        grid.setColumnOrder(columns.stream().map(grid::getColumnByKey).toList());
    }

    private List<String> columns() {
        var columns = new ArrayList<>(visibleColumns());
        columns.add(EDIT_COLUMN);
        return columns;
    }
}
