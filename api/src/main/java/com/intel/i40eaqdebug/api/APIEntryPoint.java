package com.intel.i40eaqdebug.api;

import com.intel.i40eaqdebug.api.header.CommandStruct;
import com.intel.i40eaqdebug.api.header.CommandField;
import com.intel.i40eaqdebug.api.header.Errors;
import com.intel.i40eaqdebug.api.logs.LogAdapter;
import com.intel.i40eaqdebug.api.logs.LogEntry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Public facing entry point, initialization handled internally
 */
public final class APIEntryPoint {
    private static boolean INIT;
    private static Errors ERRORS;
    private static Map<Short, String> COMMANDNAMES;
    private static Map<Short, CommandStruct> COMMANDSTRUCTS;
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
    static void init(Errors errors, Map<Short, String> names, Map<Short, CommandStruct> commands, LogAdapter adapter) {
        INIT = true;
        ERRORS = errors;
        COMMANDNAMES = names;
        COMMANDSTRUCTS = commands;
        ADAPTER = adapter;
    }

    public static String getErrorString(byte errFlag) {
        if (!INIT) {
            throw new IllegalStateException("Attempted to call API methods before initialization");
        }
        return ERRORS.getByByte(errFlag);
    }

    public static String getCommandName(short opcode) {
        if (!INIT) {
            throw new IllegalStateException("Attempted to call API methods before initialization");
        }
        return COMMANDNAMES.getOrDefault(opcode, "UNKNOWN");
    }

    public static CommandStruct getCommandStruct(short opcode) {
        if (!INIT) {
            throw new IllegalStateException("Attempted to call API methods before initialization");
        }
        return COMMANDSTRUCTS.getOrDefault(opcode, UNKNOWN_COMMAND_STRUCT);
    }

    public static Queue<LogEntry> getCommandLogQueue(File file) {
        if (!INIT) {
            throw new IllegalStateException("Attempted to call API methods before initialization");
        }
        return ADAPTER.getEntriesSequential(file);
    }

    // Stuff that allows us to return an unknown match without returning null/still be able to display raw bytes

    private static class UnknownCommandStruct implements CommandStruct {

        public String getName() {
            return "UNKNOWN";
        }

        public Map<String, CommandField> getFieldsFromBuf(byte[] buff) {
            HashMap<String, CommandField> fieldMap = new HashMap<String, CommandField>();
            fieldMap.put("UNKNOWN_FIELD", new UnknownCommandField(buff));
            return fieldMap;
        }
    }


    private static class UnknownCommandField implements CommandField {

        private final byte[] buf;

        public UnknownCommandField(byte[] buf) {
            this.buf = buf;
        }

        public String getValueAsString(byte[] buf) {
            return Util.bytesToHex(buf);
        }

        public int getStartPos() {
            return 0;
        }

        public int getEndPos() {
            return buf.length - 1;
        }

        public EndianState getEndianness() {
            return EndianState.BIG;
        }
    }

}
