package com.intel.i40eaqdebug.api.logs;

public interface LogEntry {
    byte getErr();
    short getFlags();
    short getOpCode();
    short getRetVal();
    int getCookieHigh();
    int getCookieLow();

    byte[] getBuffer();
}
