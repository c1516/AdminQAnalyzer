package com.intel.i40eaqdebug.api.logs;

import java.io.File;
import java.util.Queue;

public interface LogAdapter {
    Queue<LogEntry> getEntriesSequential(File f);
}
