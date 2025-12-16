package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseLeaderboard {

    private static final String URL = "jdbc:mysql://localhost:3306/snake_game";
    private static final String USER = "root";
    private static final String PASS = "";

    public static class LeaderboardEntry {
        public String username;
        public int score;
        public String time_elapsed;

        public LeaderboardEntry(String username, int score, String time_elapsed) {
            this.username = username;
            this.score = score;
            this.time_elapsed = time_elapsed;
        }
    }

    //Top 10 global scores
    public List<LeaderboardEntry> getTop10() {
        List<LeaderboardEntry> entries = new ArrayList<>();

        String sql = "SELECT username, score, time_elapsed " +
                     "FROM leaderboard " +
                     "ORDER BY score DESC LIMIT 10";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                entries.add(new LeaderboardEntry(
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getString("time_elapsed")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public Integer getUserId(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
   //User's personal high score
    public LeaderboardEntry getUserBest(String username) {
        String sql = "SELECT username, score, time_elapsed " +
                     "FROM leaderboard WHERE username = ? LIMIT 1";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new LeaderboardEntry(
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getString("time_elapsed")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveScore(String username, int score, String timeElapsed) {

        Integer userId = getUserId(username);
        if (userId == null) {
            System.out.println("ERROR: username not found in users table: " + username);
            return;
        }

        String sql = "INSERT INTO leaderboard (user_id, username, score, time_elapsed) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, username);
            stmt.setInt(3, score);
            stmt.setString(4, timeElapsed);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void submitScore(String username, int score, String timeElapsed) {

        String checkSQL = "SELECT score FROM leaderboard WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement check = conn.prepareStatement(checkSQL)) {

            check.setString(1, username);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                int oldScore = rs.getInt("score");

                // Only update if the new score is better
                if (score > oldScore) {
                    String updateSQL =
                        "UPDATE leaderboard SET score = ?, time_elapsed = ? WHERE username = ?";
                    PreparedStatement update = conn.prepareStatement(updateSQL);
                    update.setInt(1, score);
                    update.setString(2, timeElapsed);
                    update.setString(3, username);
                    update.executeUpdate();
                }
                return;
            }

            // Insert new record if none exists
            String insertSQL =
                "INSERT INTO leaderboard (username, score, time_elapsed) VALUES (?, ?, ?)";
            PreparedStatement insert = conn.prepareStatement(insertSQL);
            insert.setString(1, username);
            insert.setInt(2, score);
            insert.setString(3, timeElapsed);
            insert.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
