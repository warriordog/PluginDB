package net.acomputerdog.plugindb.test;

import net.acomputerdog.plugindb.DBSettings;
import net.acomputerdog.plugindb.PluginDB;
import net.acomputerdog.plugindb.db.Database;
import net.acomputerdog.plugindb.query.Query;
import net.acomputerdog.plugindb.query.SyncMode;
import org.junit.*;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class GetTest {
    private static PluginDB pdb;
    private static Database db;

    private static int freeID = 0;

    @BeforeClass
    public static void connect() throws Exception {
        Runtime.getRuntime().exec("./cleanup.sh");

        pdb = new PluginDB(new TestPlugin());
        DBSettings settings = new DBSettings();
        settings.setAutoBuildDB(true);
        settings.setDatabasePath("testdb");
        settings.setSemiSyncEnabled(false);
        pdb.load(settings);
        pdb.connect();
        db = pdb.getDatabase();
    }

    @AfterClass
    public static void disconnect() throws Exception {
        pdb.disconnect();
    }

    @Before
    public void setup() throws Exception {
        db.executeUpdate(new Query(db.prepareStatement(
                "INSERT INTO table1(col_i1, col_i2, col_s1, col_s2) " +
                "VALUES (0, 0, '0', '0')," +
                        "(1, -1, '1', '-1')," +
                        "(2, -2, '2', '-2')," +
                        "(3, -3, '3', '-3')"
        ), SyncMode.SYNC), numRows -> assertEquals(4, numRows));

        db.executeUpdate(new Query(db.prepareStatement(String.format(
                "INSERT INTO table2(id_key, col_i1) " +
                        "VALUES (%d, 0)," +
                        "(%d, 1)," +
                        "(%d, 4)," +
                        "(%d, 9)"
                , freeID, freeID + 1, freeID + 2, freeID + 3)
        ), SyncMode.SYNC), numRows -> assertEquals(4, numRows));


        db.executeUpdate(new Query(db.prepareStatement(
                "INSERT INTO table3(diff_id_key, col_s1) " +
                        "VALUES (0, '0')," +
                        "(1, '1')," +
                        "(4, '4')," +
                        "(9, '9')"
        ), SyncMode.SYNC), numRows -> assertEquals(4, numRows));

        freeID += 4;
    }

    @After
    public void teardown() {
        // do in order so we don't mess up foreign keys
        db.executeUpdate(new Query(db.prepareStatement("DELETE FROM table3"), SyncMode.SYNC), numRows -> assertEquals(4, numRows));
        db.executeUpdate(new Query(db.prepareStatement("DELETE FROM table2"), SyncMode.SYNC), numRows -> assertEquals(4, numRows));
        db.executeUpdate(new Query(db.prepareStatement("DELETE FROM table1"), SyncMode.SYNC), numRows -> assertEquals(4, numRows));
    }

    @Test
    public void getTable1() throws Exception {
        db.executeQuery(new Query(db.prepareStatement("SELECT * FROM table1"), SyncMode.SYNC), results -> {
            try {
                results.next();
                assertEquals(0, results.getInt(1));
                assertEquals(0, results.getInt(2));
                assertEquals(0, results.getInt(3));
                // extra spaces for fixed-width string
                assertEquals("0                   ", results.getString(4));
                assertEquals("0", results.getString(5));
                results.next();
                assertEquals(1, results.getInt(1));
                assertEquals(1, results.getInt(2));
                assertEquals(-1, results.getInt(3));
                // extra spaces for fixed-width string
                assertEquals("1                   ", results.getString(4));
                assertEquals("-1", results.getString(5));
                results.next();
                assertEquals(2, results.getInt(1));
                assertEquals(2, results.getInt(2));
                assertEquals(-2, results.getInt(3));
                // extra spaces for fixed-width string
                assertEquals("2                   ", results.getString(4));
                assertEquals("-2", results.getString(5));
                results.next();
                assertEquals(3, results.getInt(1));
                assertEquals(3, results.getInt(2));
                assertEquals(-3, results.getInt(3));
                // extra spaces for fixed-width string
                assertEquals("3                   ", results.getString(4));
                assertEquals("-3", results.getString(5));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void getTable2() throws Exception {
        db.executeQuery(new Query(db.prepareStatement("SELECT * FROM table2"), SyncMode.SYNC), results -> {
            try {
                results.next();
                assertEquals(freeID - 4, results.getInt(1));
                assertEquals(0, results.getInt(2));
                results.next();
                assertEquals(freeID - 3, results.getInt(1));
                assertEquals(1, results.getInt(2));
                results.next();
                assertEquals(freeID - 2, results.getInt(1));
                assertEquals(4, results.getInt(2));
                results.next();
                assertEquals(freeID - 1, results.getInt(1));
                assertEquals(9, results.getInt(2));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void getTable3() throws Exception {
        db.executeQuery(new Query(db.prepareStatement("SELECT * FROM table3"), SyncMode.SYNC), results -> {
            try {
                results.next();
                assertEquals(0, results.getInt(1));
                assertEquals("0", results.getString(2));
                results.next();
                assertEquals(1, results.getInt(1));
                assertEquals("1", results.getString(2));
                results.next();
                assertEquals(4, results.getInt(1));
                assertEquals("4", results.getString(2));
                results.next();
                assertEquals(9, results.getInt(1));
                assertEquals("9", results.getString(2));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
