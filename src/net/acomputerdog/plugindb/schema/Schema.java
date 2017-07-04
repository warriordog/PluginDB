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

    public static Schema createFromFile(InputStream in) throws IOException {
        List<Table> tables = new ArrayList<>();
        while (in.available() > 0) {
            String line = readTable(in);

            int eq = line.indexOf('=');
            if (!isValidAfter(eq, line.length())) {
                throw new CfgException("Misplaced '=' in line: " + line);
            }

            Table table = new Table(line.substring(0, eq));

            String defs = line.substring(eq + 1);
            String[] fieldDefs = defs.split("\\|");

            List<Column> fieldList = new ArrayList<>();
            for (String def : fieldDefs) {
                String[] defParts = def.split(",");

                if (defParts.length < 2) {
                    throw new CfgException("Missing type for field '" + def + "' in line: " + line);
                }

                String fieldName = defParts[0];
                FType fieldType = FType.fromFullString(defParts[1]);
                String fieldTypeMod = FType.getTypeMod(defParts[1]);

                if (fieldType == null) {
                    throw new CfgException("Unknown field type '" + defParts[1] + "' in line: " + line);
                }

                boolean nullable = true;
                boolean primary = false;
                String foreignKey = null;
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

                        foreignKey = defPart.substring(fStart + 1, fEnd);

                        // convert to format expected for other parts of code
                        foreignKey = foreignKey.replace('.', '(');
                        foreignKey = foreignKey + ")";
                    }
                }

                fieldList.add(new Column(table, fieldName, fieldType, fieldTypeMod, nullable, primary, foreignKey));
            }
            table.setColumns(fieldList.toArray(new Column[fieldList.size()]));
            tables.add(table);
        }
        return new Schema(tables.toArray(new Table[tables.size()]));
    }

    private static String readTable(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();
        int ch;
        do {
            ch = in.read();

            // skip whitespace
            if (!Character.isWhitespace(ch)) {
                builder.append((char) ch);
            }
            // skip comments
            if (ch == '#') {
                // read until end of line
                while (ch != '\n') {
                    ch = in.read();
                }
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
