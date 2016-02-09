package com.intel.i40eaqdebug.backend;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.header.CommandStruct;
import com.intel.i40eaqdebug.api.header.Errors;
import com.intel.i40eaqdebug.api.logs.LogAdapter;
import com.intel.i40eaqdebug.backend.header.ErrorImpl;
import com.intel.i40eaqdebug.backend.header.HeaderParser;
import com.intel.i40eaqdebug.backend.header.LogParser;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Initial entry point for backend stuff
 */
public class BackendMain {

    static File HEADER_FILE = new File("i40e_adminq_cmd.h");
    static File OPC_DEF_FILE = new File("opcode_to_struct.txt");


    public static void main(String... args)
        throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        copyResources();
        Map<String, CommandStruct> structs = HeaderParser.parseCommandStructs(HEADER_FILE);
        Map<Integer, String> opcIntToString = HeaderParser.constructShortToOPC(HEADER_FILE);
        Errors error = new ErrorImpl(HEADER_FILE);
        Map<Integer, CommandStruct> opcIntToStruct = HeaderParser.constructOPCShortToStruct(opcIntToString, OPC_DEF_FILE, structs);

        // Some reflection to bypass private access to method
        Method m = APIEntryPoint.class.getDeclaredMethod("init", Errors.class, Map.class, Map.class, LogAdapter.class);
        m.setAccessible(true);
        m.invoke(null, error, opcIntToString, opcIntToStruct, new LogParser());
    }

    private static void copyResources() throws IOException {
        // Check for files
        if (!HEADER_FILE.exists()) {
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(BackendMain.class.getResourceAsStream("/i40e_adminq_cmd.h")));
            copy(HEADER_FILE, reader);
        }
        // Check for files
        if (!OPC_DEF_FILE.exists()) {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(BackendMain.class.getResourceAsStream("/opcode_to_struct.txt")));
            copy(OPC_DEF_FILE, reader);
        }
    }

    private static void copy(File file, BufferedReader reader) throws IOException {
        FileWriter copyWriter = new FileWriter(file);
        String s;
        while ((s = reader.readLine()) != null) {
            copyWriter.append(s).append("\n");
        }
        copyWriter.flush();
    }

}
