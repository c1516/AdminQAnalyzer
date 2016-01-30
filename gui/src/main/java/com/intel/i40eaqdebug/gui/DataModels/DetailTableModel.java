package com.intel.i40eaqdebug.gui.DataModels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetailTableModel {
    private final StringProperty OpCode;

    public DetailTableModel() {
        this(null);
    }

    public DetailTableModel(String opCode) {
        this.OpCode = new SimpleStringProperty(opCode);
    }

    public String getOpCode() {return OpCode.get();}
    public StringProperty getOpCodeProperty() {return OpCode;}
    public void setOpCode(String newOpCode) {OpCode.set(newOpCode);}
}
