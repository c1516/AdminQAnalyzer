package com.intel.i40eaqdebug.gui.DataModels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetailTableModel {
    private final StringProperty OpCode;
    private final StringProperty Flags;
    private final StringProperty ErrorCode;
    private final StringProperty ReturnCode;
    private final StringProperty CookieHigh;
    private final StringProperty CookieLow;

    public DetailTableModel() {
        this(null, null, null, null, null, null);
    }

    public DetailTableModel(String OpCode, String Flags, String Error, String Return, String CookieHigh, String CookieLow) {
        this.OpCode = new SimpleStringProperty(OpCode);
        this.Flags = new SimpleStringProperty(Flags);
        this.ErrorCode = new SimpleStringProperty(Error);
        this.ReturnCode = new SimpleStringProperty(Return);
        this.CookieHigh = new SimpleStringProperty(CookieHigh);
        this.CookieLow = new SimpleStringProperty(CookieLow);
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

    public String getCookieHigh() {return CookieHigh.get();}
    public StringProperty getCookieHighProperty() {return CookieHigh;}
    public void setCookieHigh(String newCookieHigh) {CookieHigh.set(newCookieHigh);}

    public String getCookieLow() {return CookieLow.get();}
    public StringProperty getCookieLowProperty() {return CookieLow;}
    public void setCookieLow(String newCookieLow) {CookieLow.set(newCookieLow);}
}
