package com.intel.i40eaqdebug.api.header;

public class TimeStamp {
    private long seconds;
    private long nanos;

    public TimeStamp(long seconds, long nanos) {
        this.seconds = seconds;
        this.nanos = nanos;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getNanos() {
        return nanos;
    }

    @Override public String toString() {
        return Long.toString(seconds) + "." + Long.toString(nanos);
    }
}
