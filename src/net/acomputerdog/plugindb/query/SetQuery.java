package net.acomputerdog.plugindb.query;

import net.acomputerdog.plugindb.schema.Table;
import net.acomputerdog.plugindb.schema.field.Field;

public class SetQuery extends Query {
    private final Table table;
    private final Field[] fields;

    private final String conditions;

    public SetQuery(Table table, String conditions, Field... fields) {
        super(false);
        this.table = table;
        this.conditions = conditions;

        this.fields = fields;
    }

    public Field getFieldIdx(int idx) {
        return fields[idx];
    }

    public Field getFieldNum(int num) {
        return getFieldIdx(num - 1);
    }

    public Table getTable() {
        return table;
    }

    public int getNumFields() {
        return fields.length;
    }

    public String getConditions() {
        return conditions;
    }
}
