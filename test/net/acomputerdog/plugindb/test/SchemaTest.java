package net.acomputerdog.plugindb.test;

import net.acomputerdog.plugindb.DBSettings;
import net.acomputerdog.plugindb.PluginDB;
import net.acomputerdog.plugindb.schema.Schema;
import net.acomputerdog.plugindb.schema.Table;
import net.acomputerdog.plugindb.schema.field.FType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SchemaTest {
    private PluginDB db;

    private Schema schema;

    private Table t1;
    private Table t2;
    private Table t3;

    @Before
    public void setUp() throws Exception {
        Runtime.getRuntime().exec("./cleanup.sh");

        db = new PluginDB(new TestPlugin());
        DBSettings settings = new DBSettings();
        settings.setAutoBuildDB(true);
        settings.setDatabasePath("testdb");
        settings.setSemiSyncEnabled(false);
        db.load(settings);
        schema = db.getSchema();
    }

    @Before
    public void init() throws Exception {
        assertEquals(3, schema.getTables().length);
        t1 = schema.getTables()[0];
        t2 = schema.getTables()[1];
        t3 = schema.getTables()[2];
    }

    @Test
    public void table1ColNames() throws Exception {
        assertEquals(5, t1.getNumColumns());

        assertEquals("id_key", t1.getColumnNum(1).getName());
        assertEquals("col_i1", t1.getColumnNum(2).getName());
        assertEquals("col_i2", t1.getColumnNum(3).getName());
        assertEquals("col_s1", t1.getColumnNum(4).getName());
        assertEquals("col_s2", t1.getColumnNum(5).getName());
    }

    @Test
    public void table1ColTypes() throws Exception {
        assertEquals(FType.IDENTITY, t1.getColumnNum(1).getType());
        assertEquals(FType.INTEGER, t1.getColumnNum(2).getType());
        assertEquals(FType.INTEGER, t1.getColumnNum(3).getType());
        assertEquals(FType.STRING, t1.getColumnNum(4).getType());
        assertEquals(FType.VARSTRING, t1.getColumnNum(5).getType());
    }

    @Test
    public void table1Col1Flags() throws Exception {
        assertTrue(t1.getColumnNum(1).isPrimaryKey());
        assertFalse(t1.getColumnNum(1).isNullable());
        assertNull(t1.getColumnNum(1).getForeignKey());
        assertFalse(t1.getColumnNum(1).isUnique());
        assertNull(t1.getColumnNum(1).getTypeMod());
    }

    @Test
    public void table1Col2Flags() throws Exception {
        assertFalse(t1.getColumnNum(2).isPrimaryKey());
        assertTrue(t1.getColumnNum(2).isNullable());
        assertNull(t1.getColumnNum(2).getForeignKey());
        assertFalse(t1.getColumnNum(2).isUnique());
        assertNull(t1.getColumnNum(2).getTypeMod());
    }

    @Test
    public void table1Col3Flags() throws Exception {
        assertFalse(t1.getColumnNum(3).isPrimaryKey());
        assertFalse(t1.getColumnNum(3).isNullable());
        assertNull(t1.getColumnNum(3).getForeignKey());
        assertFalse(t1.getColumnNum(3).isUnique());
        assertNull(t1.getColumnNum(3).getTypeMod());
    }

    @Test
    public void table1Col4Flags() throws Exception {
        assertFalse(t1.getColumnNum(4).isPrimaryKey());
        assertTrue(t1.getColumnNum(4).isNullable());
        assertNull(t1.getColumnNum(4).getForeignKey());
        assertFalse(t1.getColumnNum(4).isUnique());
        assertEquals("20", t1.getColumnNum(4).getTypeMod());
    }

    @Test
    public void table1Col5Flags() throws Exception {
        assertFalse(t1.getColumnNum(5).isPrimaryKey());
        assertTrue(t1.getColumnNum(5).isNullable());
        assertNull(t1.getColumnNum(5).getForeignKey());
        assertFalse(t1.getColumnNum(5).isUnique());
        assertEquals("20", t1.getColumnNum(5).getTypeMod());
    }

    @Test
    public void table2ColNames() throws Exception {
        assertEquals(2, t2.getNumColumns());

        assertEquals("id_key", t2.getColumnNum(1).getName());
        assertEquals("col_i1", t2.getColumnNum(2).getName());
    }

    @Test
    public void table2ColTypes() throws Exception {
        assertEquals(FType.INTEGER, t2.getColumnNum(1).getType());
        assertEquals(FType.INTEGER, t2.getColumnNum(2).getType());
    }

    @Test
    public void table2Col1Flags() throws Exception {
        assertTrue(t2.getColumnNum(1).isPrimaryKey());
        assertFalse(t2.getColumnNum(1).isNullable());
        assertEquals("table1(id_key)", t2.getColumnNum(1).getForeignKey());
        assertFalse(t2.getColumnNum(1).isUnique());
        assertNull(t2.getColumnNum(1).getTypeMod());
    }

    @Test
    public void table2Col2Flags() throws Exception {
        assertFalse(t2.getColumnNum(2).isPrimaryKey());
        assertFalse(t2.getColumnNum(2).isNullable());
        assertNull(t2.getColumnNum(2).getForeignKey());
        assertTrue(t2.getColumnNum(2).isUnique());
        assertNull(t2.getColumnNum(2).getTypeMod());
    }

    @Test
    public void table3ColNames() throws Exception {
        assertEquals(2, t3.getNumColumns());

        assertEquals("diff_id_key", t3.getColumnNum(1).getName());
        assertEquals("col_s1", t3.getColumnNum(2).getName());
    }

    @Test
    public void table3ColTypes() throws Exception {
        assertEquals(FType.INTEGER, t3.getColumnNum(1).getType());
        assertEquals(FType.VARSTRING, t3.getColumnNum(2).getType());
    }

    @Test
    public void table3Col1Flags() throws Exception {
        assertTrue(t3.getColumnNum(1).isPrimaryKey());
        assertFalse(t3.getColumnNum(1).isNullable());
        assertEquals("table2(col_i1)", t3.getColumnNum(1).getForeignKey());
        assertFalse(t3.getColumnNum(1).isUnique());
        assertNull(t3.getColumnNum(1).getTypeMod());
    }

    @Test
    public void table3Col2Flags() throws Exception {
        assertFalse(t3.getColumnNum(2).isPrimaryKey());
        assertFalse(t3.getColumnNum(2).isNullable());
        assertNull(t3.getColumnNum(2).getForeignKey());
        assertFalse(t3.getColumnNum(2).isUnique());
        assertEquals("30", t3.getColumnNum(2).getTypeMod());
    }
}
