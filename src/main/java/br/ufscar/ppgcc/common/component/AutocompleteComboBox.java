package br.ufscar.ppgcc.common.component;

import com.vaadin.flow.component.combobox.ComboBox;

import java.util.List;
import java.util.function.Function;

import static java.util.Objects.nonNull;

public abstract class AutocompleteComboBox<T> extends ComboBox<T> {

    protected AutocompleteComboBox(Function<String, List<T>> searchFunction) {
        setHelperText("Type at least 5 characters and press enter");
        setClearButtonVisible(true);
        setAllowCustomValue(true);
        setManualValidation(true);
        addCustomValueSetListener(input -> {
            var customValue = input.getDetail();
            if (nonNull(customValue) && customValue.length() > 4) {
                setItems(searchFunction.apply(customValue));
                setInvalid(false);
                setOpened(true);
            } else {
                setErrorMessage("Invalid input text.");
                setInvalid(true);
            }
        });
    }

    @Override
    public void setValue(T value) {
        if (nonNull(value) && !getListDataView().contains(value)) {
            setItems(List.of(value));
        }
        super.setValue(value);
    }
}
