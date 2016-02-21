package com.intel.i40eaqdebug.api.logs;

import com.intel.i40eaqdebug.api.header.TimeStamp;

public interface LogEntry {

    boolean isAsync();

    boolean isWriteback();

    int getStartLine();

    TimeStamp getTimeStamp();

    int getErr();

    short getFlags();

    int getOpCode();

    short getRetVal();

    int getCookieHigh();

    int getCookieLow();

    int[] getParams();

    int[] getAddr();

    byte[] getBuffer();
}
