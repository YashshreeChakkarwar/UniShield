package com.unishield;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * ============================================================
 * DATABASE MANAGER - MySQL JDBC Connection & Operations
 * ============================================================
 * Manages connection to MySQL database for the Learning System.
 * Stores and retrieves attack history for pattern recognition.
 * Connection is optional — app works without MySQL too.
 */
public class DatabaseManager {

    // ---- Connection parameters ----
    private String url;
    private String username;
    private String password;
    private Connection connection;
    private boolean connected;

    // ---- Inner class for attack records ----
    public static class AttackRecord {
        public int id;
        public String sourceNode;
        public String path;
        public String actionTaken;
        public int infectedCount;
        public double damageReduction;
        public String timestamp;

        @Override
        public String toString() {
            return String.format("[%s] Source: %s | Path: %s | Action: %s | Infected: %d | Reduction: %.1f%%",
                    timestamp, sourceNode, path, actionTaken, infectedCount, damageReduction);
        }
    }

    // ---- Constructor with default local MySQL settings ----
    public DatabaseManager() {
        this.url = "jdbc:mysql://localhost:3306/unishield_db";
        this.username = "root";
        this.password = "uhsay@root";
        this.connected = false;
    }

    // ---- Constructor with custom settings ----
    public DatabaseManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.connected = false;
    }

    // ============================================================
    // CONNECT - Establish connection to MySQL database
    // ============================================================
    public boolean connect() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Attempt to connect
            connection = DriverManager.getConnection(url, username, password);
            connected = true;

            // Create table if it doesn't exist
            createTableIfNotExists();

            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
            connected = false;
            return false;
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            connected = false;
            return false;
        }
    }

    // ============================================================
    // CREATE TABLE - Create AttackHistory table if not exists
    // ============================================================
    private void createTableIfNotExists() {
        if (!connected) return;

        String createSQL = "CREATE TABLE IF NOT EXISTS AttackHistory (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "sourceNode VARCHAR(50) NOT NULL, " +
                "path TEXT NOT NULL, " +
                "actionTaken TEXT NOT NULL, " +
                "infectedCount INT DEFAULT 0, " +
                "damageReduction DOUBLE DEFAULT 0.0, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createSQL);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    // ============================================================
    // SAVE ATTACK HISTORY - Store a new attack record
    // ============================================================
    public boolean saveAttackHistory(String sourceNode, String path,
                                     String actionTaken, int infectedCount,
                                     double damageReduction) {
        if (!connected) return false;

        String insertSQL = "INSERT INTO AttackHistory " +
                "(sourceNode, path, actionTaken, infectedCount, damageReduction) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(insertSQL);
            pstmt.setString(1, sourceNode);
            pstmt.setString(2, path);
            pstmt.setString(3, actionTaken);
            pstmt.setInt(4, infectedCount);
            pstmt.setDouble(5, damageReduction);
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error saving attack history: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // GET ATTACK HISTORY - Retrieve all past attack records
    // ============================================================
    public ArrayList<AttackRecord> getAttackHistory() {
        ArrayList<AttackRecord> records = new ArrayList<>();
        if (!connected) return records;

        String selectSQL = "SELECT * FROM AttackHistory ORDER BY timestamp DESC";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(selectSQL);

            // Manually iterate through result set
            while (rs.next()) {
                AttackRecord record = new AttackRecord();
                record.id = rs.getInt("id");
                record.sourceNode = rs.getString("sourceNode");
                record.path = rs.getString("path");
                record.actionTaken = rs.getString("actionTaken");
                record.infectedCount = rs.getInt("infectedCount");
                record.damageReduction = rs.getDouble("damageReduction");
                record.timestamp = rs.getString("timestamp");
                records.add(record);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error retrieving attack history: " + e.getMessage());
        }

        return records;
    }

    // ============================================================
    // FIND SIMILAR ATTACKS - Search for attacks from same source
    // ============================================================
    public ArrayList<AttackRecord> findSimilarAttacks(String sourceNode) {
        ArrayList<AttackRecord> similar = new ArrayList<>();
        if (!connected) return similar;

        String selectSQL = "SELECT * FROM AttackHistory WHERE sourceNode = ? ORDER BY timestamp DESC";

        try {
            PreparedStatement pstmt = connection.prepareStatement(selectSQL);
            pstmt.setString(1, sourceNode);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AttackRecord record = new AttackRecord();
                record.id = rs.getInt("id");
                record.sourceNode = rs.getString("sourceNode");
                record.path = rs.getString("path");
                record.actionTaken = rs.getString("actionTaken");
                record.infectedCount = rs.getInt("infectedCount");
                record.damageReduction = rs.getDouble("damageReduction");
                record.timestamp = rs.getString("timestamp");
                similar.add(record);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error finding similar attacks: " + e.getMessage());
        }

        return similar;
    }

    // ============================================================
    // GET BEST ACTION - Find the most effective past action
    // ============================================================
    public String getBestPastAction(String sourceNode) {
        if (!connected) return null;

        ArrayList<AttackRecord> similar = findSimilarAttacks(sourceNode);

        if (similar.isEmpty()) return null;

        // Find the record with highest damage reduction
        AttackRecord best = similar.get(0);
        for (int i = 1; i < similar.size(); i++) {
            if (similar.get(i).damageReduction > best.damageReduction) {
                best = similar.get(i);
            }
        }

        return "Past best action for " + sourceNode + ": " + best.actionTaken +
               " (achieved " + String.format("%.1f", best.damageReduction) + "% reduction)";
    }

    // ============================================================
    // CONNECTION STATUS
    // ============================================================
    public boolean isConnected() {
        return connected;
    }

    // ============================================================
    // DISCONNECT - Close database connection
    // ============================================================
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connected = false;
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // ============================================================
    // SET CREDENTIALS - Update connection parameters
    // ============================================================
    public void setCredentials(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
}
