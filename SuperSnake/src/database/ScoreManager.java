package database;

import java.sql.*;
import java.util.ArrayList;

public class ScoreManager {

    private Connection connection;
    private Statement statement;

    public ScoreManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/snake_game",
                "root",
                ""
            );
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save score + update recent score table
    public void saveScore(int userId, int score, String time) {
        try {
            String insertSQL = "INSERT INTO scores (user_id, score, time_elapsed) VALUES (" +
                                userId + ", " + score + ", '" + time + "')";
            statement.executeUpdate(insertSQL);

            // Update users most recent score
            String recentSQL = "INSERT INTO recent_scores (user_id, recent_score, recent_time_elapsed) " +
                               "VALUES (" + userId + ", " + score + ", '" + time + "') " +
                               "ON DUPLICATE KEY UPDATE recent_score = " + score +
                               ", recent_time_elapsed = '" + time + "'";
            statement.executeUpdate(recentSQL);

            // Trim to top 10 scores per user
            trimScores(userId);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Keep only top 10 scores (delete the rest)
    private void trimScores(int userId) {
        try {
            String deleteExtra = 
                "DELETE FROM scores WHERE score_id NOT IN (" +
                "SELECT score_id FROM (" +
                "   SELECT score_id FROM scores " +
                "   WHERE user_id = " + userId +
                "   ORDER BY score DESC LIMIT 10" +
                ") AS keep_scores" +
                ") AND user_id = " + userId;

            statement.executeUpdate(deleteExtra);
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Player’s top 10
    public ResultSet getUserTop10(int userId) {
        try {
            String sql = "SELECT * FROM scores WHERE user_id = " + userId + 
                         " ORDER BY score DESC LIMIT 10";
            return statement.executeQuery(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Player’s most recent score
    public ResultSet getRecentScore(int userId) {
        try {
            String sql = "SELECT * FROM recent_scores WHERE user_id = " + userId;
            return statement.executeQuery(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Global Top 10 Leaderboard
    public ResultSet getLeaderboardTop10() {
        try {
            String sql = "SELECT * FROM leaderboard_top10";
            return statement.executeQuery(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Close connection
    public void close() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
