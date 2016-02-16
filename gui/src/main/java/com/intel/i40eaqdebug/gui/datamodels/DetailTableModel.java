package com.intel.i40eaqdebug.gui.datamodels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
This just represents one line in our table. Since
each line will only contain a Name and Value, that's
all our model needs to have.
*/
public class DetailTableModel {
    private final StringProperty Name;
    private final StringProperty Value;

    public DetailTableModel() {
        this(null, null);
    }

    public DetailTableModel(String Name, String Value) {
        this.Name = new SimpleStringProperty(Name);
        this.Value = new SimpleStringProperty(Value);
    }


    public String getName() {
        return Name.get();
    }

    public void setName(String newOpCode) {
        Name.set(newOpCode);
    }

    public StringProperty getNameProperty() {
        return Name;
    }

    public String getValue() {
        return Value.get();
    }

    public void setValue(String newFlags) {
        Value.set(newFlags);
    }

    public StringProperty getValueProperty() {
        return Value;
    }
}
