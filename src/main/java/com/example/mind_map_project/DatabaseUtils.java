package com.example.mind_map_project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mindmapdatabase";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "2107118"; // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean registerUser(String username, String email, String password) {
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveMindMap(String username, String name, String data) {
        String insertOrUpdateQuery = """
        INSERT INTO mindmaps (username, name, data)
        VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE
        data = VALUES(data)
    """;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(insertOrUpdateQuery)) {
            statement.setString(1, username);
            statement.setString(2, name);
            statement.setString(3, data);
            return statement.executeUpdate() > 0; // Returns true if rows were inserted/updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static List<String[]> getMindMaps(String username) {
        String query = "SELECT name, data FROM mindmaps WHERE username = ?";
        List<String[]> mindMaps = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                mindMaps.add(new String[]{
                        resultSet.getString("name"), // Mind map name
                        resultSet.getString("data")  // Serialized JSON data
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mindMaps;
    }


    public static String loadMindMap(String username, String mindMapName) {
        // Updated query to remove the unnecessary JOIN and use username directly
        String query = "SELECT data FROM mindmaps WHERE username = ? AND name = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username); // Set username in the query
            statement.setString(2, mindMapName); // Set mind map name in the query
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("data"); // Return the JSON data
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log any SQL exception
        }
        return null; // Return null if no data is found
    }

    public static List<String[]> getAllUserMindMaps() {
        String query = "SELECT username, name FROM mindmaps";
        List<String[]> userData = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String name = resultSet.getString("name");
                userData.add(new String[]{username, name});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userData;
    }
    public static boolean deleteMindMap(String username, String mindMapName) {
        String query = "DELETE FROM mindmaps WHERE username = ? AND name = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, mindMapName);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
