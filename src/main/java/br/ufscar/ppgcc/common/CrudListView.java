package br.ufscar.ppgcc.common;

import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudGrid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class CrudListView<T> extends VerticalLayout {

    private static final String EDIT_COLUMN = "vaadin-crud-edit-column";

    private final Crud<T> crud;

    public CrudListView(CrudDataProvider<T> dataProvider, Class<T> beanType) {
        var grid = new CrudGrid<>(beanType, false);
        crud = new Crud<>(beanType, grid, createEditor());
        crud.setSizeFull();

        setupDataProvider(dataProvider);
        setupGrid();

        add(crud);
        setSizeFull();
    }

    protected abstract CrudEditor<T> createEditor();

    protected abstract List<String> visibleColumns();

    private void setupDataProvider(CrudDataProvider<T> dataProvider) {
        crud.setDataProvider(dataProvider);
        crud.addDeleteListener(deleteEvent -> dataProvider.delete(deleteEvent.getItem()));
        crud.addSaveListener(saveEvent -> dataProvider.persist(saveEvent.getItem()));
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
