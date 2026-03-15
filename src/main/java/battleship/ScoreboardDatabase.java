package battleship;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class ScoreboardDatabase {

    private static final String DB_URL = "jdbc:sqlite:scoreboard.db";

    public static void initializeDatabase() {
        String sql = """
                CREATE TABLE IF NOT EXISTS scoreboard (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    winner TEXT NOT NULL,
                    loser TEXT NOT NULL,
                    moves INTEGER NOT NULL,
                    played_at TEXT NOT NULL
                );
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao inicializar a base de dados do scoreboard.");
            e.printStackTrace();
        }
    }

    public static void saveGameResult(String winner, String loser, int moves, String playedAt) {
        String sql = "INSERT INTO scoreboard (winner, loser, moves, played_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, winner);
            pstmt.setString(2, loser);
            pstmt.setInt(3, moves);
            pstmt.setString(4, playedAt);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao guardar resultado no scoreboard.");
            e.printStackTrace();
        }
    }

    public static void printScoreboard() {
        String sql = "SELECT winner, loser, moves, played_at FROM scoreboard ORDER BY id DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println();
            System.out.println("=============== SCOREBOARD ===============");

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                String winner = rs.getString("winner");
                String loser = rs.getString("loser");
                int moves = rs.getInt("moves");
                String playedAt = rs.getString("played_at");

                System.out.println("Vencedor: " + winner);
                System.out.println("Derrotado: " + loser);
                System.out.println("Jogadas: " + moves);
                System.out.println("Data: " + playedAt);
                System.out.println("------------------------------------------");
            }

            if (!hasResults) {
                System.out.println("Ainda não existem jogos registados.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao consultar o scoreboard.");
            e.printStackTrace();
        }
    }
}