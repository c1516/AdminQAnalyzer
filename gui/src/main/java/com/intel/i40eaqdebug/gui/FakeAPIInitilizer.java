package com.intel.i40eaqdebug.gui;

import com.intel.i40eaqdebug.api.APIEntryPoint;
import com.intel.i40eaqdebug.api.header.CommandField;
import com.intel.i40eaqdebug.api.header.CommandStruct;
import com.intel.i40eaqdebug.api.header.Errors;
import com.intel.i40eaqdebug.api.logs.LogAdapter;
import com.intel.i40eaqdebug.api.logs.LogEntry;

import java.io.File;
import java.util.*;

/**
 * Created by andrey on 1/26/2016.
 */
public class FakeAPIInitilizer {
    public static void InitApi() {
        FakeErrors errors = new FakeErrors();
        Map<Integer, String> opcodeMap = new LinkedHashMap<Integer, String>();
        opcodeMap.put(0x0A00, "Opcode");
        opcodeMap.put(0x0A07, "Cooler_Opcode");

        Map<Integer, CommandStruct> commandMap = new LinkedHashMap<Integer, CommandStruct>();
        commandMap.put(0x0A00, new FakeCommandStruct(true));
        commandMap.put(0x0A07, new FakeCommandStruct(false));

        FakeLogAdaptor adaptor = new FakeLogAdaptor();

        APIEntryPoint.init(errors, opcodeMap, commandMap, adaptor);
    }

    public static class FakeErrors implements Errors {
        @Override
        public String getByByte(byte err) {
            switch (err) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
                case 3:
                    return "Bad Thing";
                case 4:
                    return "Worse Thing";
                case 5:
                    return "Baddest Thing in existance!!!";
                default:
                    return "Unknown Error";
            }
        }
    }

    public static class FakeLogAdaptor implements LogAdapter {

        @Override
        public Queue<LogEntry> getEntriesSequential(File f, int low, int high) {
            Queue<LogEntry> logs = new LinkedList<LogEntry>();

            for (int i = 0; i < 20; i++) {
                logs.add(new FakeAPIInitilizer.FakeLogLine());
            }


            return logs;
        }
    }

    public static class FakeCommandStruct implements CommandStruct {
        private LinkedHashMap<String, CommandField> fields = new LinkedHashMap<String, CommandField>();
        private boolean WhichOne = false;

        public FakeCommandStruct(boolean selector) {
            WhichOne = selector;
            for (int i = 0; i < 4; i++) {
                fields.put("field" + Integer.toString(i), new FakeCommandField(i));
            }

        }

        @Override
        public String getName() {
            if (WhichOne) {
                return "FirstCommandStruct";
            } else {
                return "SecondCommandStruct";
            }
        }

        @Override
        public LinkedHashMap<String, CommandField> getFields() {
            return fields;
        }
    }

    public static class FakeCommandField implements CommandField {
        private int WhichOne = 0;

        public FakeCommandField(int selector) {
            WhichOne = selector;
        }

        @Override
        public String getValueAsString(byte[] raw) {
            switch (WhichOne) {
                case 0:
                    return "Amazing Altruistic Anaconda";
                case 1:
                    return "Bouncy Broken Bull";
                case 2:
                    return "Cute Crazy Cat";
                case 3:
                    return "Dank Derpy Dog";
                default:
                    return "Zany Zanky Zebra";
            }
        }

        @Override
        public int getStartPos() {
            return 50;
        }

        @Override
        public int getEndPos() {
            return 200;
        }

        @Override
        public EndianState getEndianness() {
            return EndianState.BIG;
        }
    }

    public static class FakeLogLine implements LogEntry {
        private Random rand = new Random();

        private byte Error;
        private short Flags;
        private short OpCode;
        private short RetVal;
        private int CookieH;
        private int CookieL;

        public FakeLogLine() {
            Error = (byte)rand.nextInt(6);
            Flags = (short)rand.nextInt(255);
            if (rand.nextBoolean()) {
                OpCode = 0x0A00;
            } else {
                OpCode = 0x0A07;
            }

            RetVal = (short)rand.nextInt(255);
            CookieH = rand.nextInt();
            CookieL = rand.nextInt();
        }

        @Override
        public int getStartLine() {
            return 0;
        }

        @Override
        public byte getErr() {
            return Error;
        }

        @Override
        public short getFlags() {
            return Flags;
        }

        @Override
        public short getOpCode() {

            return OpCode;
        }

        @Override
        public short getRetVal() {
            return RetVal;
        }

        @Override
        public int getCookieHigh() {
            return CookieH;
        }

        @Override
        public int getCookieLow() {
            return CookieL;
        }

        @Override
        public byte[] getBuffer() {
            return new byte[0];
        }
    }
}

