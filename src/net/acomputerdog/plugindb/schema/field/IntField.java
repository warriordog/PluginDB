package net.acomputerdog.plugindb.schema.field;

import net.acomputerdog.plugindb.schema.Column;
import net.acomputerdog.plugindb.schema.Table;

public class IntField extends Field {
    private final int value;

    public IntField(Table table, Column column, int value) {
        super(table, column);
        this.value = value;
    }

    public int getInt() {
        return value;
    }
}
