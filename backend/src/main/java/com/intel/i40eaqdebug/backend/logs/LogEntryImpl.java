package com.intel.i40eaqdebug.backend.logs;

import com.intel.i40eaqdebug.api.logs.LogEntry;

import java.math.BigInteger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.ByteArrayOutputStream;


public class LogEntryImpl implements LogEntry {

    private static final Pattern BUFF_PATTERN = Pattern.compile("i40e\\s+[0-9a-f:]*[0-9a-f]+\\.[0-9a-f]+:\\s+0x([0-9a-f]+)(\\s+[0-9a-f]+)+", Pattern.CASE_INSENSITIVE);
    private static final Pattern HEADER_PATTERN = Pattern.compile("i40e\\s+[0-9a-f:]*[0-9a-f]+\\.[0-9a-f]+\\s+AQ\\s+CMD:\\s+opcode\\s+0x([0-9a-f]+),\\s+flags\\s+0x([0-9a-f]+),\\s+datalen\\s+0x([0-9a-f]+),\\s+retval\\s+0x([0-9a-f]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern COOKIE_PATTERN = Pattern.compile("i40e\\s+[0-9a-f:]*[0-9a-f]+\\.[0-9a-f]+\\s+(cookie|param|addr)\\s+\\(.,.\\)\\s+0x([0-9a-f]+)\\s+0x([0-9a-f]+)", Pattern.CASE_INSENSITIVE);

    private int lineNum;
    private int cookie[] = {0, 0};
    private int param[] = {0, 0};
    private int addr[] = {0, 0};
    private byte err = 0;
    private short opcode = 0;
    private short flags = 0;
    private long datalen = 0;
    private short retval = 0;
    private byte buffer[] = null;

    // Input data of the general form:
    // \[9999.9999\] i40e [999:999:]999.99: <data>
    // <data> is:
    //  AQ CMD: opcode 0x999, flags 0x999, datalen 0x999, retval 0x999
    //    cookie (h,l) 0x999 0x999
    //    param (0,1) 0x999 0x999
    //    addr (h,l) 0x999 0x999
    // 0x9999 99 99 99 99 99 ...   <- bytes may have garbage sign extension but are 8 bit values

    public LogEntryImpl(int startLine, String[] rawLogData) throws java.io.IOException {
        lineNum = startLine;
        ByteArrayOutputStream buff = new ByteArrayOutputStream();

        for (String logInputLine : rawLogData) {
            Matcher isMainHeader = HEADER_PATTERN.matcher(logInputLine);
            if (isMainHeader.find()) {
                opcode = (short) Integer.parseInt(isMainHeader.group(1), 16);
                flags = (short) Integer.parseInt(isMainHeader.group(2), 16);
                datalen = new BigInteger(isMainHeader.group(3), 16).longValue();
                retval = (short) Integer.parseInt(isMainHeader.group(4), 16);
            }
            else {
                Matcher isCookieLine = COOKIE_PATTERN.matcher(logInputLine);
                if (isCookieLine.find()) {
                    int a = new BigInteger(isCookieLine.group(2), 16).intValue();
                    int b = new BigInteger(isCookieLine.group(3), 16).intValue();

                    if (isCookieLine.group(1).equals("cookie")) {
                        cookie[0] = a;
                        cookie[1] = b;
                    }
                    else if (isCookieLine.group(1).equals("param")) {
                        param[0] = a;
                        param[1] = b;
                    }
                    else if (isCookieLine.group(1).equals("addr")) {
                        addr[0] = a;
                        addr[1] = b;
                    }
                    // Otherwise, we just ignore this silently.
                }
                else {
                    Matcher isBufferLine = BUFF_PATTERN.matcher(logInputLine);
                    if (isBufferLine.find()) {
                        // group 1 is the address, which we will ignore here for simplicity
                        // group 2 is a space-separated group of hex bytes (although they may be erroneously sign-extended
                        // to a larger integer value, which we will truncate back to 8 bits here)
                        for (String byteString : isBufferLine.group(2).split("\\s+")) {
                            buff.write(Integer.parseInt(byteString, 16) & 0xff);
                        }
                    }
                }
            }
        }
        buffer = buff.toByteArray();
    }

    public int getStartLine() {
        return lineNum;
    }

    public byte   getErr()        { return err;       }
    public short  getFlags()      { return flags;     }
    public short  getOpCode()     { return opcode;    }
    public short  getRetVal()     { return retval;    }
    public int    getCookieHigh() { return cookie[0]; }
    public int    getCookieLow()  { return cookie[1]; }
    public byte[] getBuffer()     { return buffer;    }
}
