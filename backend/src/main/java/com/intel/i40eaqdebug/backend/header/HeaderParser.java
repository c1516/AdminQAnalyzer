package com.intel.i40eaqdebug.backend.header;

import com.intel.i40eaqdebug.api.header.CommandField;
import com.intel.i40eaqdebug.api.header.CommandStruct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeaderParser {

    static Pattern OPCODEPATTERN = Pattern.compile("(i40e_aqc_opc_[a-z_]+)\\s+=\\s(0x[A-F0-9]+)");
    static Pattern COMMANDSTRUCTPATTERN = Pattern.compile("struct\\s+(i40e_aq(?:c)?_[a-z_]+) \\{([^}]+)};");
    static Pattern FIELDPARSEPATTERN = Pattern.compile(
        "(/\\*(?:[\\w\\s+=;\\n])+\\*/)|(#define\\s+([\\w\\d]+)\\s+([\\w\\d]+))|(([\\w\\d]+)\\s+([\\w\\d]+)(?:\\[(\\d+)\\])?);");
    // Can't match #defines with shifted values, might look into implementations of that (perhaps by building a value table?)

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

    public static Map<Integer, CommandStruct[]> constructOPCShortToStruct(Map<Integer, String> opcShortToString,
        File opcodesFile, Map<String, CommandStruct> structs) throws IOException {
        Map<String, Integer> invert = new HashMap<String, Integer>();
        for (Map.Entry<Integer, String> e : opcShortToString.entrySet()) {
            invert.put(e.getValue(), e.getKey());
        }
        BufferedReader reader = new BufferedReader(new FileReader(opcodesFile));
        String ln;
        Map<Integer, CommandStruct[]> ret = new HashMap<>();
        while ((ln = reader.readLine()) != null) {
            String[] parse = ln.split(",");
            if (parse.length < 2) {
                continue;
            }
            String opcName = parse[0].replaceAll(" ", "");
            String structName = parse[1].replaceAll(" ", "");

            String[] possibleStructNames = structName.split("\\|");
            if (possibleStructNames.length > 1) {
                CommandStruct struct1 = structs.get(possibleStructNames[0]);
                CommandStruct struct2 = structs.get(possibleStructNames[1]);
                if (struct1 == null || struct2 == null) {
                    System.out.println("Unknown struct mapping: " + parse[0] + " to " + parse[1]);
                    continue;
                }
                ret.put(invert.get(opcName),
                    new CommandStruct[] {structs.get(possibleStructNames[0]), structs.get(possibleStructNames[1])});
            } else {
                CommandStruct struct = structs.get(structName);
                if (struct == null) {
                    System.out.println("Unknown struct mapping: " + parse[0] + " to " + parse[1]);
                    continue;
                }
                ret.put(invert.get(opcName), new CommandStruct[] {structs.get(structName)});
            }
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
            if (m.group(1) != null) {
                continue; // comment
            }
            if (m.group(2) == null) { // Not a #define, so this is a field
                String type = m.group(6);
                String name = m.group(7);
                // Figure out if this is an array
                int mult;
                if (m.group(8) != null) {
                    mult = Integer.valueOf(m.group(8));
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
                String valueDef = m.group(3);
                Integer value = Integer.valueOf(m.group(4).replace("0x", ""), 16);
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
