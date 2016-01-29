package com.intel.i40eaqdebug.backend.header;

import com.intel.i40eaqdebug.api.logs.LogEntry;

import java.io.BufferedReader;
import java.io.IOException;

public class LogParser {

    public LogEntry readDiscreteEntry(BufferedReader reader) throws IOException {
        boolean isInBody = true;
        String line;
        while ((line = reader.readLine()) != null) {

        }
        return null;
    }

}
