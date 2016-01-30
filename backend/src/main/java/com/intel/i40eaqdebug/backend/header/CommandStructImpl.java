package com.intel.i40eaqdebug.backend.header;

import com.intel.i40eaqdebug.api.header.CommandField;
import com.intel.i40eaqdebug.api.header.CommandStruct;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public LinkedHashMap<String, CommandField> getFields() {
        return fields;
    }
}
