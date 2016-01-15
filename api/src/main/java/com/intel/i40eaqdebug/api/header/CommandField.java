package com.intel.i40eaqdebug.api.header;

public interface CommandField {
    // Will merely return as hex, possible expansions are translating said hex into text
    String getValueAsString();
    int getStartPos();
    int getEndPos();

    EndianState getEndianness();

    enum EndianState {
        BIG, LITTLE;
    }
}
