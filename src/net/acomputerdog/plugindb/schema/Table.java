package net.acomputerdog.plugindb.schema;

public class Table {
    private final String name;

    private Column[] columns;

    public Table(String name) {
        this.name = name;
    }

    public void setColumns(Column ... columns) {
        if (columns == null) {
            columns = new Column[0];
        }
        this.columns = columns;
    }

    public int getNumColumns() {
        return columns.length;
    }

    public Column getColumnNum(int num) {
        return getColumnIdx(num - 1);
    }

    public Column getColumnIdx(int idx) {
        return columns[idx];
    }

    public String getName() {
        return name;
    }
}
