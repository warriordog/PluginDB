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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Table{name=");
        if (name != null) {
            builder.append(name);
        } else {
            builder.append("null");
        }

        builder.append(", columns=");
        if (columns != null) {
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                Column c = columns[i];
                if (c != null) {
                    builder.append(c.toString());
                } else {
                    builder.append("null");
                }
            }
        } else {
            builder.append("null");
        }
        builder.append('}');
        return builder.toString();
    }
}
