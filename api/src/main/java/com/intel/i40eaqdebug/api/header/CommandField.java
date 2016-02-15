package com.intel.i40eaqdebug.api.header;

public interface CommandField {
    // Will merely return as hex, possible expansions are translating said hex into text
    String getValueAsString(byte[] raw);

    /**
     * @return The starting byte of this field, inclusive
     */
    int getStartPos();

    /**
     * @return The ending byte of this field, exclusive
     */
    int getEndPos();

    EndianState getEndianness();

    enum EndianState {
        BIG, LITTLE
    }
}
