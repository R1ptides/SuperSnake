import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded");

            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/snake_game",
                "root",
                ""
            );
            System.out.println("Connected to MySQL!");
        } 
        catch (ClassNotFoundException e) {
            System.out.println("Driver not found!");
            e.printStackTrace();
        } 
        catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
