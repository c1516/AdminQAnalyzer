package com.intel.i40eaqdebug.gui.DataModels;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.header.TimeStamp;
import javafx.beans.property.*;


public class TableModel {
    private final StringProperty LineNumber;
    private final StringProperty OpCode;
    private final StringProperty Flags;
    private final StringProperty ErrorCode;
    private final ObjectProperty<TimeStamp> TimeStamp;
    private final IntegerProperty OpCodeInt;

    public TableModel() { this(null, null, 0, null, null); }

    public TableModel( TimeStamp TheTime, String LineNumber, int OpCode, String Flags, String Error) {
        this.LineNumber = new SimpleStringProperty(LineNumber);
        this.OpCode = new SimpleStringProperty(APIEntryPoint.getCommandName(OpCode));
        this.OpCodeInt = new SimpleIntegerProperty(OpCode);
        this.Flags = new SimpleStringProperty(Flags);
        this.ErrorCode = new SimpleStringProperty(Error);
        this.TimeStamp = new SimpleObjectProperty<TimeStamp>(TheTime);
    }

    public boolean hasPartialValue(String val) {
        if (val == null) return false;

        if (OpCode.get().contains(val) || Flags.get().contains(val)
                || ErrorCode.get().contains(val) || (TimeStamp.get()).toString().contains(val)
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

    public TimeStamp getTimeStamp() {return TimeStamp.get();}
    public ObjectProperty<TimeStamp> getTimeStampProperty() {return TimeStamp;}
    public void setTimeStamp(TimeStamp newTimeStamp) {TimeStamp.set(newTimeStamp);}

    private void setOpCodeInt(int newOpCode) {OpCodeInt.set(newOpCode);}
    private IntegerProperty getOpCodeIntProperty() {return OpCodeInt;}
    private int getOpCodeInt() {return OpCodeInt.get();}

    //TODO: make this better.
    @Override
    public String toString() {
        return TimeStamp.get().toString() + ", " + LineNumber.get().toString() + ", " + OpCode.get().toString()
                + ", " + Flags.get().toString() + ", " + ErrorCode.get().toString();
    }
}
