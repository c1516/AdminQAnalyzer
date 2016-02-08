package com.intel.i40eaqdebug.gui.DataModels;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableModel {
    private final StringProperty LineNumber;
    private final StringProperty OpCode;
    private final StringProperty Flags;
    private final StringProperty ErrorCode;
    private final StringProperty ReturnCode;
    private final IntegerProperty OpCodeInt;

    public TableModel() { this(null, 0, null, null, null); }

    public TableModel(String LineNumber, int OpCode, String Flags, String Error, String Return) {
        this.LineNumber = new SimpleStringProperty(LineNumber);
        this.OpCode = new SimpleStringProperty(APIEntryPoint.getCommandName(OpCode));
        this.OpCodeInt = new SimpleIntegerProperty(OpCode);
        this.Flags = new SimpleStringProperty(Flags);
        this.ErrorCode = new SimpleStringProperty(Error);
        this.ReturnCode = new SimpleStringProperty(Return);

    }

    public boolean hasPartialValue(String val) {
        if (val == null) return false;

        if (OpCode.get().contains(val) || Flags.get().contains(val)
                || ErrorCode.get().contains(val) || ReturnCode.get().contains(val)
                || Integer.toHexString(OpCodeInt.get()).toUpperCase().contains(val.toUpperCase()))
             return true;
        else
            return false;
    }
    
    public String getLineNumber() {return LineNumber.get();}
    public StringProperty getLineNumberProperty() {return LineNumber;}
    public void setLineNumber(String newLineNumber) {LineNumber.set(newLineNumber);}

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

    private void setOpCodeInt(int newOpCode) {OpCodeInt.set(newOpCode);}
    private IntegerProperty getOpCodeIntProperty() {return OpCodeInt;}
    private int getOpCodeInt() {return OpCodeInt.get();}
}
