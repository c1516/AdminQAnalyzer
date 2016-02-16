package com.intel.i40eaqdebug.gui.datamodels;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.header.TimeStamp;
import javafx.beans.property.*;


public class TableModel {
    private final StringProperty LineNumber;
    private final StringProperty OpCode;
    private final IntegerProperty Flags;
    private final StringProperty ErrorCode;
    private final ObjectProperty<TimeStamp> TimeStamp;
    private final IntegerProperty OpCodeInt;
    private final BooleanProperty IsWriteBack;

    public TableModel() {
        this(null, null, 0, 0, null, false);
    }

    public TableModel(TimeStamp TheTime, String LineNumber, int OpCode, int Flags, String Error, boolean IsWriteBack) {
        this.LineNumber = new SimpleStringProperty(LineNumber);
        this.OpCode = new SimpleStringProperty(APIEntryPoint.getCommandName(OpCode));
        this.OpCodeInt = new SimpleIntegerProperty(OpCode);
        this.Flags = new SimpleIntegerProperty(Flags);
        this.ErrorCode = new SimpleStringProperty(Error);
        this.TimeStamp = new SimpleObjectProperty<TimeStamp>(TheTime);
        this.IsWriteBack = new SimpleBooleanProperty(IsWriteBack);
    }

    public boolean hasPartialValue(String val) {
        if (val == null)
            return false;

        if (OpCode.get().contains(val) || Integer.toHexString(Flags.get()).contains(val) || ErrorCode.get()
            .contains(val) || (TimeStamp.get()).toString().contains(val) || Integer.toHexString(OpCodeInt.get())
            .toUpperCase().contains(val.toUpperCase()) || LineNumber.get().contains(val))
            return true;
        else
            return false;
    }

    public String getLineNumber() {
        return LineNumber.get();
    }

    public void setLineNumber(String newLineNumber) {
        LineNumber.set(newLineNumber);
    }

    public StringProperty getLineNumberProperty() {
        return LineNumber;
    }

    public String getOpCode() {
        return OpCode.get() + " - 0x" + Integer.toHexString(OpCodeInt.get()).toUpperCase();
    }

    public void setOpCode(String newOpCode) {
        OpCode.set(newOpCode);
    }

    public StringProperty getOpCodeProperty() {
        return OpCode;
    }

    public Integer getFlags() {
        return Flags.get();
    }

    public void setFlags(int newFlags) {
        Flags.set(newFlags);
    }

    public IntegerProperty getFlagsProperty() {
        return Flags;
    }

    public String getErrorCode() {
        return ErrorCode.get();
    }

    public void setErrorCode(String newErrorCode) {
        ErrorCode.set(newErrorCode);
    }

    public StringProperty getErrorCodeProperty() {
        return ErrorCode;
    }

    public TimeStamp getTimeStamp() {
        return TimeStamp.get();
    }

    public void setTimeStamp(TimeStamp newTimeStamp) {
        TimeStamp.set(newTimeStamp);
    }

    public ObjectProperty<TimeStamp> getTimeStampProperty() {
        return TimeStamp;
    }

    private IntegerProperty getOpCodeIntProperty() {
        return OpCodeInt;
    }

    private int getOpCodeInt() {
        return OpCodeInt.get();
    }

    private void setOpCodeInt(int newOpCode) {
        OpCodeInt.set(newOpCode);
    }

    public BooleanProperty getIsWriteBackProperty() {
        return IsWriteBack;
    }

    public Boolean getIsWriteBack() {
        return IsWriteBack.get();
    }

    public void setIsWriteBack(boolean newCallback) {
        IsWriteBack.set(newCallback);
    }

    @Override public String toString() {
        return TimeStamp.get().toString() + ", " + LineNumber.get().toString() + ", " + OpCode.get().toString() + ", "
            + Integer.toHexString(Flags.get()) + ", " + ErrorCode.get().toString() + ", " + IsWriteBack.get();
    }
}
