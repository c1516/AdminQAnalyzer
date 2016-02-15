package com.intel.i40eaqdebug.api;

import com.intel.i40eaqdebug.api.header.CommandField;
import com.intel.i40eaqdebug.api.header.CommandStruct;
import com.intel.i40eaqdebug.api.header.Errors;
import com.intel.i40eaqdebug.api.logs.LogAdapter;
import com.intel.i40eaqdebug.api.logs.LogEntry;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Public facing entry point, initialization handled internally
 */
public final class APIEntryPoint {
    private static boolean INIT;
    private static Errors ERRORS;
    private static Map<Integer, String> COMMANDNAMES;
    private static Map<Integer, CommandStruct[]> COMMANDSTRUCTS;
    private static LogAdapter ADAPTER;
    private static CommandStruct UNKNOWN_COMMAND_STRUCT;

    static {
        INIT = false;
        UNKNOWN_COMMAND_STRUCT = new UnknownCommandStruct();
    }

    /**
     * Called by backend to initialize the API with valid options - consistency of this method is not guaranteed
     *
     * @param errors
     * @param commands
     */
    static void init(Errors errors, Map<Integer, String> names, Map<Integer, CommandStruct[]> commands, LogAdapter adapter) {
        INIT = true;
        ERRORS = errors;
        COMMANDNAMES = names;
        COMMANDSTRUCTS = commands;
        ADAPTER = adapter;
    }

    public static String getErrorString(int errFlag) {
        if (!INIT) {
            throw new IllegalStateException("Attempted to call API methods before initialization");
        }
        return ERRORS.getByInt(errFlag);
    }

    public static String getCommandName(int opcode) {
        if (!INIT) {
            throw new IllegalStateException("Attempted to call API methods before initialization");
        }
        return COMMANDNAMES.getOrDefault(opcode, "UNKNOWN");
    }

    public static CommandStruct getCommandStruct(int opcode, boolean isWriteback) {
        if (!INIT) {
            throw new IllegalStateException("Attempted to call API methods before initialization");
        }
        CommandStruct[] possible = COMMANDSTRUCTS.get(opcode);
        if (possible == null) {
            return UNKNOWN_COMMAND_STRUCT;
        } else if (possible.length > 1) {
            return isWriteback ? possible[0] : possible[1];
        } else {
            return possible[0];
        }
    }

    public static Queue<LogEntry> getCommandLogQueue(File file, int startIndex, int count) {
        if (!INIT) {
            throw new IllegalStateException("Attempted to call API methods before initialization");
        }
        return ADAPTER.getEntriesSequential(file, startIndex, count);
    }

    // Stuff that allows us to return an unknown match without returning null/still be able to display raw bytes


    private static class UnknownCommandStruct implements CommandStruct {

        public String getName() {
            return "UNKNOWN";
        }

        @Override public int getSize() {
            return 16;
        }

        public LinkedHashMap<String, CommandField> getFields() {
            LinkedHashMap<String, CommandField> fieldMap = new LinkedHashMap<String, CommandField>();
            fieldMap.put("UNKNOWN_FIELD", new UnknownCommandField());
            return fieldMap;
        }
    }


    private static class UnknownCommandField implements CommandField {

        public UnknownCommandField() {
        }

        public String getValueAsString(byte[] buf) {
            return Util.bytesToHex(buf);
        }

        public int getStartPos() {
            return 0;
        }

        public int getEndPos() {
            return Integer.MAX_VALUE;
        } // Impossible to know length here

        public EndianState getEndianness() {
            return EndianState.BIG;
        }
    }

}
