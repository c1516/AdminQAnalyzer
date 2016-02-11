package com.intel.i40eaqdebug.backend.header;

import com.intel.i40eaqdebug.api.header.Errors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorImpl implements Errors {

    static Pattern ERRENUMPATTERN = Pattern.compile("enum i40e_admin_queue_err \\{([^}]+)};");
    static Pattern ERRENUMENTPATTERN = Pattern.compile("(I40E_AQ_RC_\\w+)\\s+=\\s([0-9]+)");

    private Map<Integer, String> intToName;

    public ErrorImpl(File file) throws IOException {
        intToName = new HashMap<>();
        Matcher m = ERRENUMPATTERN.matcher(readFile(file));
        while (m.find()) {
            String content = m.group(1);
            Matcher m1 = ERRENUMENTPATTERN.matcher(content);
            while (m1.find()) {
                intToName.put(Integer.valueOf(m1.group(2)), m1.group(1));
            }
        }
    }

    private static String readFile(File input) throws IOException {
        // Read entire thing in as a huge string
        StringBuilder hFileBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(input));
        String next;
        while ((next = reader.readLine()) != null) {
            hFileBuilder.append(next);
            hFileBuilder.append("\n");
        }
        reader.close();
        String hFile = hFileBuilder.toString();
        return hFile;
    }

    @Override public String getByInt(int err) {
        return intToName.getOrDefault(err, intToName.get(0));
    }

}
