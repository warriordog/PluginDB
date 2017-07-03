package net.acomputerdog.plugindb.schema;

public class Column {
    private final String name;
    private final FType type;

    public Column(String name, FType type) {
        this.name = name;
        this.type = type;
    }
}
