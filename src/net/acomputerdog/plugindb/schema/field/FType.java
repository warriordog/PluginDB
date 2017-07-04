package net.acomputerdog.plugindb.schema.field;

public enum FType {
    INT('I'),
    FLOAT('F'),
    STRING('S'),
    VARSTRING('V'),
    IDENTITY('*');

    private final char fieldCh;

    FType(char fieldCh) {
        this.fieldCh = fieldCh;
    }

    public char getFieldCh() {
        return fieldCh;
    }
}
