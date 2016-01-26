package com.intel.i40eaqdebug.backend.header;

import java.io.File;

public class LogParser {
    private static String TEMP_FILE_PREFIX = "TEMP_";
    // Removes junk data from the file line by line
    public File parseJunk(File file) {
        File out = new File(TEMP_FILE_PREFIX + file.getName());

    }
}
