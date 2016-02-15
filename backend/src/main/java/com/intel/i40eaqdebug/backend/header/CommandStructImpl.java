package com.intel.i40eaqdebug.backend.header;

import com.intel.i40eaqdebug.api.header.CommandField;
import com.intel.i40eaqdebug.api.header.CommandStruct;

import java.util.LinkedHashMap;

public class CommandStructImpl implements CommandStruct {

    private final String name;
    private final LinkedHashMap<String, CommandField> fields;

    public CommandStructImpl(String name, LinkedHashMap<String, CommandField> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    @Override public int getSize() {
        int size = 0;
        for (CommandField f : fields.values()) {
            size += f.getEndPos() - f.getStartPos();
        }
        return size;
    }

    public LinkedHashMap<String, CommandField> getFields() {
        return fields;
    }
}
