package net.acomputerdog.plugindb.schema.field;

import net.acomputerdog.plugindb.schema.Column;
import net.acomputerdog.plugindb.schema.Table;

public abstract class Field<T> {
    private final Table table;
    private final Column column;

    private T value;

    public Field(Table table, Column column) {
        this.table = table;
        this.column = column;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
