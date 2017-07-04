package net.acomputerdog.plugindb.schema.field;

import net.acomputerdog.plugindb.schema.Column;
import net.acomputerdog.plugindb.schema.Table;

public class FloatField extends Field {
    private final double value;

    public FloatField(Table table, Column column, double value) {
        super(table, column);
        this.value = value;
    }

    public double getDouble() {
        return value;
    }

    public float getFloat() {
        return (float)value;
    }
}
