package com.intel.i40eaqdebug.api.header;

import java.util.LinkedHashMap;

public interface CommandStruct {

    /**
     * @return the name of the command struct: use
     */
    @Deprecated String getName();

    int getSize();

    LinkedHashMap<String, CommandField> getFields();
}
