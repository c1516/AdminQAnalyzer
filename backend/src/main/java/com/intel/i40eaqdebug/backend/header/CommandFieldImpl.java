package com.intel.i40eaqdebug.backend.header;

import com.intel.i40eaqdebug.api.Util;
import com.intel.i40eaqdebug.api.header.CommandField;

import java.util.Arrays;
import java.util.Map;

public class CommandFieldImpl implements CommandField {

    private final int start;
    private final int end;
    private final EndianState endianness;
    private final Map<Integer, String> definedValues;

    public CommandFieldImpl(int start, int end, EndianState endianness, Map<Integer, String> definedValues) {
        this.start = start;
        this.end = end;
        this.endianness = endianness;
        this.definedValues = definedValues; // Not used in first release
    }

    public String getValueAsString(byte[] buf) {
        boolean reverse = endianness.equals(EndianState.LITTLE);
        byte[] specific = Arrays.copyOfRange(buf, start, end);
        if (reverse) {
            for(int i = 0; i < specific.length / 2; i++) {
                byte temp = specific[i];
                specific[i] = specific[specific.length - i - 1];
                specific[specific.length - i - 1] = temp;
            }
        }
        return Util.bytesToHex(specific); // TODO get from defined values for second release
    }

    public int getStartPos() {
        return start;
    }

    public int getEndPos() {
        return end;
    }

    public EndianState getEndianness() {
        return endianness;
    }
}
