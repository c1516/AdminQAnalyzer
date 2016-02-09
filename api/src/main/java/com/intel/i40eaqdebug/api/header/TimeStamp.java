package com.intel.i40eaqdebug.api.header;

public class TimeStamp {
    private long seconds;
    private long nanos;

    public TimeStamp(long seconds, long nanos) {
        this.seconds = seconds;
        this.nanos = nanos;
    }

    long getSeconds() {
        return seconds;
    }
    long getNanos() {
        return nanos;
    }
}
