package net.acomputerdog.plugindb.query;

import net.acomputerdog.plugindb.schema.Column;
import net.acomputerdog.plugindb.schema.Table;

public class GetQuery extends Query{
    private final Table table;
    private final Column[] wantedColumns;
    private final String conditions;

    public GetQuery(Table table, Column ... wantedColumns) {
        this(table, null, wantedColumns);
    }

    public GetQuery(Table table, String conditions, Column... wantedColumns) {
        super(true);

        this.table = table;
        this.conditions = conditions;
        this.wantedColumns = wantedColumns;
    }

    public Table getTable() {
        return table;
    }

    public Column[] getWantedColumns() {
        return wantedColumns;
    }

    public String getConditions() {
        return conditions;
    }
}
