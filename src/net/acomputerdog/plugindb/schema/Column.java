package net.acomputerdog.plugindb.schema;

import net.acomputerdog.plugindb.schema.field.FType;

public class Column {
    private final Table table;
    private final String name;

    private final FType type;
    private final String typeMod;

    private final boolean nullable;
    private final boolean primaryKey;
    private final String foreignKey;

    public Column(Table table, String name, FType type) {
        this(table, name, type, null, true, false, null);
    }

    public Column(Table table, String name, FType type, String typeMod, boolean nullable, boolean primaryKey, String foreignKey) {
        this.table = table;
        this.name = name;
        this.type = type;
        this.typeMod = typeMod;
        this.nullable = nullable;
        this.primaryKey = primaryKey;
        this.foreignKey = foreignKey;
    }

    public Table getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public FType getType() {
        return type;
    }

    public String getTypeMod() {
        return typeMod;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public String getForeignKey() {
        return foreignKey;
    }
}
