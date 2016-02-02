package com.intel.i40eaqdebug.gui.DataModels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableModel {
    private final StringProperty OpCode;
    private final StringProperty Flags;
    private final StringProperty ErrorCode;
    private final StringProperty ReturnCode;

    public TableModel() {
        this(null, null, null, null);
    }

    public TableModel(String OpCode, String Flags, String Error, String Return) {
        this.OpCode = new SimpleStringProperty(OpCode);
        this.Flags = new SimpleStringProperty(Flags);
        this.ErrorCode = new SimpleStringProperty(Error);
        this.ReturnCode = new SimpleStringProperty(Return);
    }

    public boolean hasPartialValue(String val) {
        if (val == null) return false;

        if (OpCode.get().contains(val) || Flags.get().contains(val)
                || ErrorCode.get().contains(val) || ReturnCode.get().contains(val))
             return true;
        else
            return false;
    }

    public String getOpCode() {return OpCode.get();}
    public StringProperty getOpCodeProperty() {return OpCode;}
    public void setOpCode(String newOpCode) {OpCode.set(newOpCode);}

    public String getFlags() {return Flags.get();}
    public StringProperty getFlagsProperty() {return Flags;}
    public void setFlags(String newFlags) {Flags.set(newFlags);}

    public String getErrorCode() {return ErrorCode.get();}
    public StringProperty getErrorCodeProperty() {return ErrorCode;}
    public void setErrorCode(String newErrorCode) {ErrorCode.set(newErrorCode);}

    public String getReturnCode() {return ReturnCode.get();}
    public StringProperty getReturnCodeProperty() {return ReturnCode;}
    public void setReturnCode(String newReturnCode) {ReturnCode.set(newReturnCode);}
}
