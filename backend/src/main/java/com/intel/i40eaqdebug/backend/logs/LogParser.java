package com.intel.i40eaqdebug.backend.logs;

import com.intel.i40eaqdebug.api.header.TimeStamp;
import com.intel.i40eaqdebug.api.logs.LogAdapter;
import com.intel.i40eaqdebug.api.logs.LogEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser implements LogAdapter {

    Pattern BEGIN = Pattern.compile("AQ([TR])X:\\s+desc and buffer( writeback)?");
    Pattern JUNK_FILTER = Pattern.compile("(\\[([0-9]+)\\.([0-9]+)])? (i40e .+)");

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
                    if (currLineNumber != -1) { // Init value/we just reached first one
                        parsedEntries.put(currLineNumber, currEntry.toArray(new String[currEntry.size()]));
                    }
                    currLineNumber = readLineNumber; // Update the line for next read entry
                    currEntry.clear();
                }
                if (readCount == count) { // Reached the end of desired input
                    flag = true;
                    break;
                }
                // New discrete entry we want to read - update save flag if false
                save = true;
                currEntry.add((m.group(1)).equals("R") + ""); // Add whether this is an async event to the list
                currEntry.add((m.group(2) != null) + ""); // Add whether this is a writeback to the list
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
        LinkedList<EntryRaw> out = new LinkedList<EntryRaw>();
        boolean isAsync = Boolean.valueOf(lines[0]);
        boolean isWriteback = Boolean.valueOf(lines[1]);
        for (int i = 2; i < lines.length; i++) {
            String logLine = lines[i];
            Matcher m = JUNK_FILTER.matcher(logLine);
            if (m.find()) {
                String timesec = "-1";
                String timenano = "-1";
                if (m.group(1) != null) {
                    timesec = m.group(2);
                    timenano = m.group(3);
                }
                String item = m.group(4);
                out.add(new EntryRaw(Long.valueOf(timesec), Long.valueOf(timenano), item));
            }
        }

        EntryRaw raw = out.getFirst();

        TimeStamp stamp = new TimeStamp(raw.timestampSec, raw.timestampNano);
        String[] entraw = new String[out.size()];
        int i = 0;
        for (EntryRaw r : out) {
            entraw[i] = r.item;
            i++;
        }
        LogEntry ent = new LogEntryImpl(stamp, isAsync, isWriteback, lineNum, entraw);
        System.out.println("Opcode: " + ent.getOpCode());
        System.out.println("DeviceID: " + ent.getDeviceId());
        return ent;
    }

    private class EntryRaw {
        long timestampSec;
        long timestampNano;
        String item;

        public EntryRaw(long timestampSec, long timestampNano, String item) {
            this.timestampSec = timestampSec;
            this.timestampNano = timestampNano;
            this.item = item;
        }
    }

}
