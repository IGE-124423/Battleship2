package battleship;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardDatabaseTest {

    private final String DB_FILE = "scoreboard.db";

    @BeforeEach
    void cleanDatabase() {
        File db = new File(DB_FILE);
        if (db.exists()) {
            db.delete();
        }
    }

    /**
     * Test for the initializeDatabase method
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("Test if connection isn't null and if scoreboard exists")
    void testInitializeDatabase1() throws SQLException {
        ScoreboardDatabase.initializeDatabase();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:scoreboard.db")) {
            assertNotNull(conn,"Error: Connection shuld not be null.");

            ResultSet rs = conn.getMetaData().getTables(null, null, "scoreboard", null);
            assertTrue(rs.next(), "Error: Table 'scoreboard' should exist");
        }
    }

    /**
     * Test for the initializeDatabase method
     */
    @Test
    @DisplayName("Test method's throw exception")
    void testInitializeDatabase2() {
        System.setProperty("jdbc.url", "jdbc:sqlite:/invalid/path/db.db");

        assertDoesNotThrow(() -> {ScoreboardDatabase.initializeDatabase();}, "Error: Should not trigger the catch");
    }

    /**
     * Test for the saveGameResults method
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("Checks if the game results have the right values")
    void testSaveGameResult1() throws SQLException {
        ScoreboardDatabase.initializeDatabase();

        ScoreboardDatabase.saveGameResult("Maria", "Jorge", 20, "2025-01-01");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:scoreboard.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM scoreboard")) {

            assertTrue(rs.next(),"Error: Table should exist.");
            assertEquals("Maria", rs.getString("winner"),"Error: Label should be 'Maria'.");
            assertEquals("Jorge", rs.getString("loser"),"Error: Label should be 'Jorge'." );
            assertEquals(20, rs.getInt("moves"),"Error: Label should be '20'.");
            assertEquals("2025-01-01", rs.getString("played_at"),"Error: Label should be '2025-01-01'.");
        }
    }

    /**
     * Test for the saveGameResults method (Throws and catch)
     */
    @Test
    @DisplayName("Tests method's throw/catch")
    void testSaveGameResult2() {
        ScoreboardDatabase.initializeDatabase();

        assertDoesNotThrow(() -> {
            ScoreboardDatabase.saveGameResult(null, null, -1, null);
        },"Error: Should not trigger a catch");
    }

    /**
     * Test for the printScoreboard method (With results)
     * Cyclomatic Complexity: 1
     */
    @Test
    @DisplayName("Test the method with values")
    void testPrintScoreboard1() {
        ScoreboardDatabase.initializeDatabase();
        ScoreboardDatabase.saveGameResult("Maria", "Jorge", 20, "2025-01-01");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        ScoreboardDatabase.printScoreboard();

        String result = output.toString();

        assertTrue(result.contains("Maria"), "Error: Result should include 'Maria'.");
        assertTrue(result.contains("Jorge"),"Error: Result should include 'Jorge'.");
        assertTrue(result.contains("20"),"Error: Result should include '20'.");
        assertTrue(result.contains("2025-01-01"),"Error: Result should include '2025-01-01'.");
    }

    /**
     * Test for the printScoreboard method (Without results)
     */
    @Test
    @DisplayName("Test the method without values")
    void testPrintScoreboard2() {
        ScoreboardDatabase.initializeDatabase();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        ScoreboardDatabase.printScoreboard();

        String result = output.toString();

        assertTrue(result.contains("Ainda não existem jogos registados."), "Error: There should be displayed only the no games message");
    }

    /**
     * Test for the printScoreboard method (Throws and catch)
     */
    @Test
    @DisplayName("Test the method's throw/catch")
    void testPrintScoreboard3() {
        System.setProperty("jdbc.url", "jdbc:sqlite:/invalid/path/db.db");

        assertDoesNotThrow(() -> {
            ScoreboardDatabase.printScoreboard();
        }, "Error: Should not trigger throw");
    }

}