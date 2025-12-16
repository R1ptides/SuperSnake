package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDBWrite {
    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded");

            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/snake_game",
                "root",
                ""
            );
            System.out.println("Connected to MySQL successfully!");

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String insertSQL = "INSERT INTO test_table (message) VALUES ('Hello from Java!')";
            System.out.println("Executing SQL: " + insertSQL);
            statement.executeUpdate(insertSQL);
            System.out.println("Row inserted successfully!");

            String selectSQL = "SELECT * FROM test_table";
            System.out.println("Executing SQL: " + selectSQL);
            resultSet = statement.executeQuery(selectSQL);

            System.out.println("\nCurrent test_table contents:");
            System.out.println("-----------------------------");
            System.out.printf("%-5s %-30s%n", "ID", "Message");
            System.out.println("-----------------------------");

            while (resultSet.next()) {
                System.out.printf("%-5d %-30s%n",
                    resultSet.getInt("id"),
                    resultSet.getString("message"));
            }

            System.out.println("-----------------------------");
        } 
        catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
            e.printStackTrace();
        } 
        catch (SQLException e) {
            System.out.println("SQL error!");
            e.printStackTrace();
        }

        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
            System.out.println("Database connection closed.");
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
