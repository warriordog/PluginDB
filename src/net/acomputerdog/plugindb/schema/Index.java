package net.acomputerdog.plugindb.schema;

public class Index<T> {
    private final int sqlIndex;
    private final String name;
    private T value;

    public Index(int sqlIndex, String name) {
        this.sqlIndex = sqlIndex;
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
