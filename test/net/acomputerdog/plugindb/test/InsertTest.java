package net.acomputerdog.plugindb.test;

import net.acomputerdog.plugindb.DBSettings;
import net.acomputerdog.plugindb.PluginDB;
import net.acomputerdog.plugindb.ex.QuerySQLException;
import net.acomputerdog.plugindb.query.Query;
import net.acomputerdog.plugindb.query.SyncMode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.PreparedStatement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InsertTest {
    private static PluginDB db;

    @BeforeClass
    public static void setUp() throws Exception {
        Runtime.getRuntime().exec("./cleanup.sh");

        db = new PluginDB(new TestPlugin());
        DBSettings settings = new DBSettings();
        settings.setAutoBuildDB(true);
        settings.setDatabasePath("testdb");
        settings.setSemiSyncEnabled(false);
        db.load(settings);
        db.connect();
        insertInitialRows();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        db.disconnect();
    }

    public static void insertInitialRows() throws Exception {
        PreparedStatement s1 = db.getDatabase().prepareStatement("INSERT INTO table1(col_i1, col_i2, col_s1, col_s2) VALUES(0, 0, 'aaa', 'bbb')");
        db.getDatabase().executeUpdate(new Query(s1, SyncMode.SYNC), numRows -> assertEquals(1, numRows));

        PreparedStatement s2 = db.getDatabase().prepareStatement("INSERT INTO table2(id_key, col_i1) VALUES(0, 10)");
        db.getDatabase().executeUpdate(new Query(s2, SyncMode.SYNC), numRows -> assertEquals(1, numRows));

        PreparedStatement s3 = db.getDatabase().prepareStatement("INSERT INTO table3(diff_id_key, col_s1) VALUES(10, 'AAA')");
        db.getDatabase().executeUpdate(new Query(s3, SyncMode.SYNC), numRows -> assertEquals(1, numRows));
    }

    @Test
    public void testInsertGoodNulls() throws Exception {
        PreparedStatement s = db.getDatabase().prepareStatement("INSERT INTO table1(col_i1, col_i2, col_s1, col_s2) VALUES(NULL, -1, NULL, NULL)");
        db.getDatabase().executeUpdate(new Query(s, SyncMode.SYNC), numRows -> assertEquals(1, numRows));
    }

    @Test(expected = QuerySQLException.class)
    public void testInsertBadNulls() throws Exception {
        PreparedStatement s = db.getDatabase().prepareStatement("INSERT INTO table1(col_i1, col_i2, col_s1, col_s2) VALUES(NULL, NULL, NULL, NULL)");
        db.getDatabase().executeUpdate(new Query(s, SyncMode.SYNC), numRows -> fail("Exception not thrown"));
    }

    @Test(expected = QuerySQLException.class)
    public void testInsertBadFKInT2() {
        PreparedStatement s = db.getDatabase().prepareStatement("INSERT INTO table2(id_key, col_i1) VALUES(-1, 0)");
        db.getDatabase().executeUpdate(new Query(s, SyncMode.SYNC), numRows -> fail("Exception not thrown"));
    }

    @Test(expected = QuerySQLException.class)
    public void testInsertBadFKInT3() {
        PreparedStatement s = db.getDatabase().prepareStatement("INSERT INTO table3(diff_id_key, col_s1) VALUES(-1, '')");
        db.getDatabase().executeUpdate(new Query(s, SyncMode.SYNC), numRows -> fail("Exception not thrown"));
    }
}
