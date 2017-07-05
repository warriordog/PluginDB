package net.acomputerdog.plugindb.schema;

import net.acomputerdog.plugindb.ex.CfgException;
import net.acomputerdog.plugindb.schema.field.FType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Schema {
    private final Table[] tables;

    public Schema(Table[] tables) {
        this.tables = tables;
    }

    public Table[] getTables() {
        return tables;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Schema{tables=");
        if (tables != null) {
            for (int i = 0; i < tables.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                Table t = tables[i];
                if (t != null) {
                    builder.append(t.toString());
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

    public static Schema createFromFile(InputStream in) throws IOException {
        List<Table> tables = new ArrayList<>();
        while (in.available() > 0) {
            String line = readTable(in);

            int eq = line.indexOf('=');
            if (!isValidAfter(eq, line.length())) {
                throw new CfgException("Misplaced '=' in line: " + line);
            }

            Table table = new Table(line.substring(0, eq).trim());

            String defs = line.substring(eq + 1).trim();
            String[] fieldDefs = defs.split("\\|");

            List<Column> fieldList = new ArrayList<>();
            for (String def : fieldDefs) {
                String[] defParts = def.split(",");

                if (defParts.length < 2) {
                    throw new CfgException("Missing type for field '" + def + "' in line: " + line);
                }

                String fieldName = defParts[0].trim();
                FType fieldType = FType.fromFullString(defParts[1].trim());
                String fieldTypeMod = FType.getTypeMod(defParts[1].trim());

                if (fieldType == null) {
                    throw new CfgException("Unknown field type '" + defParts[1] + "' in line: " + line);
                }

                boolean nullable = true;
                boolean primary = false;
                String foreignKey = null;
                boolean unique = false;
                for (int i = 2; i < defParts.length; i++) {
                    String defPart = defParts[i];
                    if ("NOTNULL".equals(defPart)) {
                        nullable = false;
                    } else if ("PRIMARY".equals(defPart)) {
                        primary = true;
                    } else if (defPart.startsWith("FOREIGN")) {
                        int fStart = defPart.indexOf('(');
                        int fEnd = defPart.indexOf(')');

                        if (!isValidAfter(fStart, defPart.length()) || !isValidBefore(fEnd, defPart.length())) {
                            throw new CfgException("Invalid foreign key constraint '" + defPart + "' in line: " + line);
                        }

                        foreignKey = defPart.substring(fStart + 1, fEnd).trim();

                        // convert to format expected for other parts of code
                        foreignKey = foreignKey.replace('.', '(');
                        foreignKey = foreignKey + ")";
                    } else if ("UNIQUE".equals(defPart)) {
                        unique = true;
                    }
                }

                fieldList.add(new Column(table, fieldName, fieldType, fieldTypeMod, nullable, primary, foreignKey, unique));
            }
            table.setColumns(fieldList.toArray(new Column[fieldList.size()]));
            tables.add(table);
        }
        return new Schema(tables.toArray(new Table[tables.size()]));
    }

    private static String readTable(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();

        boolean startFound = false;
        int ch;
        do {
            ch = in.read();

            // skip comments
            if (ch == '#') {
                // read until end of line
                while (ch != '\n') {
                    ch = in.read();
                }
            }

            if (startFound) {
                // skip whitespace
                if (!Character.isWhitespace(ch) && ch != ']') {
                    builder.append((char) ch);
                }
            } else if (ch == '['){
                startFound = true;
            }
        } while (ch != ']');
        return builder.toString();
    }

    private static boolean isValidBefore(int idx, int length) {
        return idx > -1 && length - idx >= 1;
    }

    private static boolean isValidAfter(int idx, int length) {
        return idx > -1 && length - idx >= 2;
    }
}
