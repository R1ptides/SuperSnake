package database;

import java.sql.*;

public class UserAuth {

    private final String URL = "jdbc:mysql://localhost:3306/snake_game";
    private final String USER = "root";
    private final String PASS = "";

    public UserAuth() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found!");
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }


    public boolean usernameExists(String username) {
        String sql = "SELECT username FROM users WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            return rs.next();  // True if username is already taken

        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Safer to assume it exists if SQL fails
        }
    }

    public boolean registerUser(String username, String password) {
        if (usernameExists(username)) {
            return false;
        }

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean login(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return false; // Username does not exist
            }

            String storedPassword = rs.getString("password");

            return storedPassword.equals(password);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean continueAsGuest() {
        return true;
    }
}
