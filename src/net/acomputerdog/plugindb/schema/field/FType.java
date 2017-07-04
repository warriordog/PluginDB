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

    public static FType fromString(String str) {
        return valueOf(str.toUpperCase());
    }

    public static FType fromFullString(String fullString) {
        int idx = fullString.indexOf('(');
        if (idx > -1) {
            return fromString(fullString.substring(0, idx));
        } else {
            return fromString(fullString);
        }
    }

    public static String getTypeMod(String fullString) {
        int idx = fullString.indexOf('(');
        int idx2 = fullString.indexOf(')');
        if (idx > -1 && fullString.length() - idx > 1 && idx2 > -1 && fullString.length() - idx2 > -1) {
            return fullString.substring(idx + 1, idx2);
        } else {
            return null;
        }
    }
}
