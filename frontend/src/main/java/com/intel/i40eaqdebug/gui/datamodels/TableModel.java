package com.intel.i40eaqdebug.gui.datamodels;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.header.TimeStamp;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import javafx.beans.property.*;


public class TableModel {
    private final StringProperty LineNumber;
    private final StringProperty OpCode;
    private final IntegerProperty Flags;
    private final StringProperty ErrorCode;
    private final ObjectProperty<TimeStamp> TimeStamp;
    private final IntegerProperty OpCodeInt;
    private final StringProperty DeviceID;
    private final BooleanProperty IsAsync;
    private final BooleanProperty IsWriteBack;
    private String HexOpCode;
    public LogEntry logLine;

    public TableModel() {
        this(null, null, 0, 0, null, null, false, false);
    }

    public TableModel(TimeStamp TheTime, String LineNumber, int OpCode, int Flags, String DeviceID, String Error, boolean IsAsync, boolean IsWriteBack) {
        this.LineNumber = new SimpleStringProperty(LineNumber);

        String hex = Integer.toHexString(OpCode).toUpperCase();
        String format = "0x%1$4" + "s";
        HexOpCode = String.format(format, hex).replace(' ', '0');

        this.OpCode = new SimpleStringProperty(APIEntryPoint.getCommandName(OpCode));
        this.OpCodeInt = new SimpleIntegerProperty(OpCode);

        this.Flags = new SimpleIntegerProperty(Flags);
        this.ErrorCode = new SimpleStringProperty(Error);
        this.TimeStamp = new SimpleObjectProperty<TimeStamp>(TheTime);
        this.DeviceID = new SimpleStringProperty(DeviceID);
        this.IsWriteBack = new SimpleBooleanProperty(IsWriteBack);
        this.IsAsync = new SimpleBooleanProperty(IsAsync);
    }

    public boolean hasPartialValue(String val) {
        if (val == null)
            return false;

        if (OpCode.get().contains(val) || Integer.toHexString(Flags.get()).contains(val) || ErrorCode.get()
                .contains(val) || (TimeStamp.get()).toString().contains(val) || HexOpCode.contains(val.toUpperCase())
                || LineNumber.get().contains(val) || Boolean.toString(IsAsync.get()).contains(val) || DeviceID.get().contains(val))
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

    public String getOpCode() { return OpCode.get(); }
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

    public String getDeviceID() {
        return DeviceID.get();
    }
    public void setDeviceID(String newDeviceID) {
        DeviceID.set(newDeviceID);
    }
    public StringProperty getDeviceIDProperty() {
        return DeviceID;
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

    public IntegerProperty getOpCodeIntProperty() {
        return OpCodeInt;
    }
    public int getOpCodeInt() {
        return OpCodeInt.get();
    }
    public void setOpCodeInt(int newOpCode) {
        OpCodeInt.set(newOpCode);
    }

    public BooleanProperty getIsAsyncProperty() {
        return IsAsync;
    }
    public Boolean getIsAsync() {
        return IsAsync.get();
    }
    public void setIsAsync(boolean newCallback) {
        IsAsync.set(newCallback);
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
                + Integer.toHexString(Flags.get()) + ", " + DeviceID.get().toString() + ", " + ErrorCode.get().toString()
                + ", " + IsWriteBack.get() + ", " + IsAsync.get();
    }
}
