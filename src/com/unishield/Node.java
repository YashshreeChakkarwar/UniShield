package com.unishield;

/**
 * ============================================================
 * NODE CLASS - Represents a device/server in the university network
 * ============================================================
 * Each node has a unique ID, a human-readable name, a type
 * (student device, lab system, or critical server), and a risk score.
 * Nodes can be infected by malware or protected by defense actions.
 */
public class Node {

    // ---- Enum: Types of nodes in the university network ----
    public enum NodeType {
        STUDENT_DEVICE,      // Personal student laptops/phones
        LAB_SYSTEM,          // Computer lab machines
        EXAM_SERVER,         // CRITICAL - Handles exam data
        ATTENDANCE_SERVER    // CRITICAL - Handles attendance records
    }

    // ---- Node Properties ----
    private String id;              // Unique identifier (e.g., "STU_1", "LAB_1")
    private String name;            // Human-readable name (e.g., "Student Device A")
    private NodeType type;          // Type of this node
    private int riskScore;          // Current risk score (0-100)
    private boolean infected;       // Whether this node is currently infected
    private boolean nodeProtected;  // Whether defense has been applied
    private int accessAttempts;     // Number of access attempts (for monitoring)
    private double xPos;            // X position for graph visualization
    private double yPos;            // Y position for graph visualization

    // ---- Constructor ----
    public Node(String id, String name, NodeType type, double xPos, double yPos) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.riskScore = 0;
        this.infected = false;
        this.nodeProtected = false;
        this.accessAttempts = 0;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    // ---- Check if this node is a critical server ----
    public boolean isCritical() {
        return type == NodeType.EXAM_SERVER || type == NodeType.ATTENDANCE_SERVER;
    }

    // ---- Getters and Setters ----

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        // Clamp risk score between 0 and 100
        if (riskScore < 0) {
            this.riskScore = 0;
        } else if (riskScore > 100) {
            this.riskScore = 100;
        } else {
            this.riskScore = riskScore;
        }
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public boolean isNodeProtected() {
        return nodeProtected;
    }

    public void setNodeProtected(boolean nodeProtected) {
        this.nodeProtected = nodeProtected;
    }

    public int getAccessAttempts() {
        return accessAttempts;
    }

    public void incrementAccessAttempts() {
        this.accessAttempts++;
    }

    public void setAccessAttempts(int accessAttempts) {
        this.accessAttempts = accessAttempts;
    }

    public double getXPos() {
        return xPos;
    }

    public double getYPos() {
        return yPos;
    }

    // ---- Reset node to clean state ----
    public void reset() {
        this.infected = false;
        this.nodeProtected = false;
        this.riskScore = 0;
        this.accessAttempts = 0;
    }

    // ---- String representation for debugging ----
    @Override
    public String toString() {
        String status = infected ? "INFECTED" : (nodeProtected ? "PROTECTED" : "NORMAL");
        return String.format("[%s] %s (%s) - Risk: %d - %s", id, name, type, riskScore, status);
    }
}
