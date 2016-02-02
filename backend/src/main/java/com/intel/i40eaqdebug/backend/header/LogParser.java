package com.intel.i40eaqdebug.backend.header;

import com.intel.i40eaqdebug.api.logs.LogAdapter;
import com.intel.i40eaqdebug.api.logs.LogEntry;
import com.intel.i40eaqdebug.backend.logs.LogEntryImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser implements LogAdapter {

    Pattern BEGIN = Pattern.compile("desc and buffer");
    Pattern ERR_RET = Pattern.compile("completed with error 0x([0-9]+)");
    Pattern JUNK_FILTER = Pattern.compile("\\[([0-9]+\\.[0-9]+)] (i40e .+)");

    public Queue<LogEntry> getEntriesSequential(File f, int startIdx, int count) {
        try {
            return readDiscreteEntries(startIdx, count, new BufferedReader(new FileReader(f)));
        } catch (Exception e) {
            e.printStackTrace(); // TODO
            return new LinkedList<LogEntry>();
        }
    }

    public Queue<LogEntry> readDiscreteEntries(int startIdx, int count, BufferedReader reader) throws IOException {
        // Conditions Check
        if (startIdx < 0) {
            throw new IllegalArgumentException("Cannot read a negative entry index!");
        }
        if (count < 1) {
            throw new IllegalArgumentException("Cannot read 0 or negative entries!");
        }
        // TODO: possibly something more efficient than a skip read until we reach the startIdx?
        int readCount = -1;
        int currLineNumber = -1;
        int readLineNumber = 0;
        String line;
        boolean save = false;
        boolean flag = false; // Exiting read loop prior to end
        LinkedList<String> currEntry = new LinkedList<String>();
        LinkedHashMap<Integer, String[]> parsedEntries = new LinkedHashMap<Integer, String[]>();
        while ((line = reader.readLine()) != null) {
            readLineNumber++;
            // Check if it is a new discrete entry
            Matcher m = BEGIN.matcher(line);
            if (m.find()) {
                // New Discrete Entry
                readCount++; // Increment number of read entries
                if (readCount < startIdx) {
                    continue; // We haven't reached the part we are interested in yet so we skip until next entry
                }
                // Check if we already have a current Entry and add to parsed entries if so
                if (readLineNumber >= 0) { // We have an index that is non-zero -> we have read in something
                    parsedEntries.put(currLineNumber, currEntry.toArray(new String[currEntry.size()]));
                    currLineNumber = readLineNumber; // Update the line for next read entry
                    currEntry.clear();
                }
                if (readCount == count) { // Reached the end of desired input
                    flag = true;
                    break;
                }
                // New discrete entry we want to read - update save flag if false
                save = true;
            } else {
                // Not a new discrete entry line
                if (save) {
                    currEntry.add(line);
                }
            }
        }
        if (!flag) { // Ended due to EOF
            parsedEntries.put(currLineNumber, currEntry.toArray(new String[currEntry.size()]));
        }
        Queue<LogEntry> ret = new LinkedList<LogEntry>();
        for (Map.Entry<Integer, String[]> entry : parsedEntries.entrySet()) {
            ret.add(produceEntry(entry.getKey(), entry.getValue()));
        }
        System.out.println(ret.size());
        return ret;
    }

    private LogEntry produceEntry(int lineNum, String[] lines) throws IOException {
        List<String> out = new LinkedList<String>();
        for (String logLine : lines) {
            Matcher m = JUNK_FILTER.matcher(logLine);
            if (m.find()) {
                String timestamp = m.group(1); // TODO actually use this
                String item = m.group(2);
                out.add(item);
            }
        }
        for (String s : out) {
            System.out.println(s);
        }
        LogEntry ent = new LogEntryImpl(lineNum, out.toArray(new String[out.size()]));
        System.out.println("Opcode: " + ent.getOpCode());
        return ent;
    }

}
