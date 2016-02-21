package com.intel.i40eaqdebug.api;

import com.intel.i40eaqdebug.api.header.CommandField;
import com.intel.i40eaqdebug.api.header.CommandStruct;
import com.intel.i40eaqdebug.api.header.Errors;
import com.intel.i40eaqdebug.api.logs.LogAdapter;
import com.intel.i40eaqdebug.api.logs.LogEntry;

import java.io.File;
import java.util.Arrays;
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
    static void init(Errors errors, Map<Integer, String> names, Map<Integer, CommandStruct[]> commands,
        LogAdapter adapter) {
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

        private LinkedHashMap<String, CommandField> fields;

        public UnknownCommandStruct() {
            fields = new LinkedHashMap<>();
            fields.put("param0", new GenericCommandField(0, 4));
            fields.put("param1", new GenericCommandField(4, 4));
            fields.put("addr0", new GenericCommandField(4, 4));
            fields.put("addr1", new GenericCommandField(4, 4));
        }

        @Override public String getName() {
            return "UNKNOWN";
        }

        @Override public int getSize() {
            return 16;
        }

        @Override public LinkedHashMap<String, CommandField> getFields() {
            return fields;
        }
    }

    private static class GenericCommandField implements CommandField {

        private int offset;
        private int length;

        public GenericCommandField(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }

        @Override public String getValueAsString(byte[] raw) {
            return Util.bytesToHex(Arrays.copyOfRange(raw, offset, offset + length));
        }

        @Override public int getStartPos() {
            return offset;
        }

        @Override public int getEndPos() {
            return offset + length;
        }

        @Override public EndianState getEndianness() {
            return EndianState.BIG;
        }
    }

}
