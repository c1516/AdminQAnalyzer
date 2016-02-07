package com.intel.i40eaqdebug.api.logs;

public interface LogEntry {

    int getStartLine();
    long getTimeStampMillis();
    byte getErr();
    short getFlags();
    short getOpCode();
    short getRetVal();
    int getCookieHigh();
    int getCookieLow();

    int[] getParams();
    int[] getAddr();

    byte[] getBuffer();
}
