package net.acomputerdog.plugindb.schema.field;

import net.acomputerdog.plugindb.schema.Column;
import net.acomputerdog.plugindb.schema.Table;

public class StringField extends Field {
    private final String value;

    public StringField(Table table, Column column, String value) {
        super(table, column);
        this.value = value;
    }

    public String getString() {
        return value;
    }
}
