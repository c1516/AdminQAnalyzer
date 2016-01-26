package com.intel.i40eaqdebug.backend.header;

import com.intel.i40eaqdebug.api.header.CommandField;
import com.intel.i40eaqdebug.api.header.CommandStruct;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeaderParser {

    static Pattern OPCODEPATTERN = Pattern.compile("(i40e_aqc_opc_[a-z_]+)\\s+=\\s(0x[A-F0-9]+)");
    static Pattern COMMANDSTRUCTPATTERN = Pattern.compile("struct (i40e_aqc_[a-z_]+) \\{([^}]+)};");
    static Pattern FIELDPARSEPATTERN = Pattern.compile("(#define\\s+([\\w\\d]+)\\s+([\\w\\d]+))|(([\\w\\d]+)\\s+([\\w\\d]+));");
    static Pattern ARRAYPATTERN = Pattern.compile("(([\\w\\d]+)\\[([\\d]+)])");

    static Map<String, Integer> TYPETOSIZE;
    static Map<String, CommandField.EndianState> TYPETOENDIAN;

    static {
        TYPETOSIZE = new HashMap<String, Integer>();
        TYPETOENDIAN = new HashMap<String, CommandField.EndianState>();
        // So far, number of different types is small enough that we can manually map, might want to automate in future somehow
        TYPETOSIZE.put("u8", 1);
        TYPETOSIZE.put("__le16", 2);
        TYPETOSIZE.put("__le32", 4);
        TYPETOENDIAN.put("u8", CommandField.EndianState.BIG);
        TYPETOENDIAN.put("__le16", CommandField.EndianState.LITTLE);
        TYPETOENDIAN.put("__le32", CommandField.EndianState.LITTLE);
    }


    public static Map<Integer, String> constructShortToOPC(File headerFile) throws IOException {
        String header = readFile(headerFile);
        Matcher m = OPCODEPATTERN.matcher(header);
        Map<Integer, String> ret = new HashMap<Integer, String>();
        while (m.find()) {
            String opcName = m.group(1);
            Integer opcVal = Integer.valueOf(m.group(2).replace("0x", ""), 16);
            ret.put(opcVal, opcName);
        }
        return ret;
    }

    public static Map<Integer, CommandStruct> constructOPCShortToStruct(Map<Integer, String> opcShortToString, File opcodesFile,
        Map<String, CommandStruct> structs) throws IOException {
        Map<String, Integer> invert = new HashMap<String, Integer>();
        for (Map.Entry<Integer, String> e : opcShortToString.entrySet()) {
            invert.put(e.getValue(), e.getKey());
        }
        BufferedReader reader = new BufferedReader(new FileReader(opcodesFile));
        String ln;
        Map<Integer, CommandStruct> ret = new HashMap<Integer, CommandStruct>();
        while ((ln = reader.readLine()) != null) {
            String[] parse = ln.split(",");
            if (parse.length < 2) {
                continue;
            }
            String opcName = parse[0];
            String structName = parse[1];
            ret.put(invert.get(opcName), structs.get(structName));
        }
        return ret;
    }

    public static Map<String, CommandStruct> parseCommandStructs(File headerFile) throws IOException {
        Map<String, CommandStruct> ret = new HashMap<String, CommandStruct>();
        String file = readFile(headerFile);
        Matcher m = COMMANDSTRUCTPATTERN.matcher(file);
        while (m.find()) {
            String structName = m.group(1);
            String fields = m.group(2);
            CommandStruct struct = constructStructFromString(structName, fields);
            ret.put(structName, struct);
        }
        return ret;
    }

    private static CommandStruct constructStructFromString(String structName, String fields) {
        LinkedHashMap<String, CommandField> fieldsByName = new LinkedHashMap<String, CommandField>();
        int offset = 0;
        int start, end;
        Matcher m = FIELDPARSEPATTERN.matcher(fields);
        // If not null then the field we are currently constructing values for has defined values
        Map<Integer, String> definedValues = null;
        while (m.find()) {
            if (m.group(1) == null) { // Not a #define, so this is a field
                String type = m.group(5);
                String name = m.group(6);
                // Figure out if this is an array
                int mult;
                Matcher am = ARRAYPATTERN.matcher(name);
                if (am.find()) {
                    name = am.group(1);
                    mult = Integer.valueOf(am.group(2));
                } else {
                    mult = 1;
                }
                int size;
                if (!TYPETOSIZE.containsKey(type)) {
                    if (type.startsWith("i40e_aqc_")) { // Is a defined command structure
                        size = 16;
                    } else {
                        System.out.println(m.group());
                        throw new RuntimeException("UNDEFINED TYPE " + type);
                    }
                } else {
                    size = TYPETOSIZE.get(type);
                }

                CommandField.EndianState endianness = TYPETOENDIAN.get(type);
                start = offset;
                end = start + size * mult; // Exclusive
                offset = end; // Update for next iteration
                CommandField field = new CommandFieldImpl(start, end, endianness, definedValues);
                fieldsByName.put(name, field);
                definedValues = null; // Reset defined values to null
            } else {
                if (definedValues == null) {
                    definedValues = new HashMap<Integer, String>();
                }
                String valueDef = m.group(2);
                Integer value = Integer.valueOf(m.group(3).replace("0x", ""), 16);
                definedValues.put(value, valueDef);
            }
        }
        return new CommandStructImpl(structName, fieldsByName);
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
}
