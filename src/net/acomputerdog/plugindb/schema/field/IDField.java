package net.acomputerdog.plugindb.schema.field;

import net.acomputerdog.plugindb.schema.Column;
import net.acomputerdog.plugindb.schema.Table;

public class IDField extends IntField {
    public IDField(Table table, Column column, int value) {
        super(table, column, value);
    }

    public int getID() {
        return getInt();
    }
}
