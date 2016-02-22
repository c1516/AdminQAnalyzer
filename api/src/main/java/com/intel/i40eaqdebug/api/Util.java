package com.intel.i40eaqdebug.api;

/**
 * Several potentially useful utility methods
 */
public final class Util {

    public static byte[] toBytes(int i) {
        byte b0 = (byte) ((i & 0xFF000000) >> 24);
        byte b1 = (byte) ((i & 0xFF0000) >> 16);
        byte b2 = (byte) ((i & 0xFF00) >> 8);
        byte b3 = (byte) (i & 0xFF);
        return new byte[] {b0, b1, b2, b3};
    }

    public static byte[] toBytes(short s) {
        byte b0 = (byte) ((s & 0xFF00) >> 8);
        byte b1 = (byte) (s & 0xFF);
        return new byte[] {b0, b1};
    }

    public static byte[] toBytes(long l) {
        byte b0 = (byte) ((l & 0xFF00000000000000L) >> 56);
        byte b1 = (byte) ((l & 0xFF000000000000L) >> 48);
        byte b2 = (byte) ((l & 0xFF0000000000L) >> 40);
        byte b3 = (byte) ((l & 0xFF00000000L) >> 32);
        byte b4 = (byte) ((l & 0xFF000000) >> 24);
        byte b5 = (byte) ((l & 0xFF0000) >> 16);
        byte b6 = (byte) ((l & 0xFF00) >> 8);
        byte b7 = (byte) (l & 0xFF);
        return new byte[] {b0, b1, b2, b3, b4, b5, b6, b7};
    }

    public static String bytesToHex(byte... packed) {
        StringBuilder sB = new StringBuilder();
        boolean init = false;
        for (byte b : packed) {
            if (init) {
                sB.append(" ");
            } else {
                init = true;
            }
            sB.append(String.format("%02X", b));
        }
        return sB.toString();
    }

    public static long repackBytesNumeric(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) + (bytes[i] & 0xFF);
        }
        return value;
    }
}
