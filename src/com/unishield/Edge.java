package com.unishield;

/**
 * ============================================================
 * EDGE CLASS - Represents a connection between two nodes
 * ============================================================
 * Edges can be WiFi connections or file-sharing links.
 * Each edge has a weight representing connection speed/risk.
 * Edges can be deactivated to simulate blocking a connection.
 */
public class Edge {

    // ---- Enum: Types of connections ----
    public enum EdgeType {
        WIFI,           // Wireless network connection
        FILE_SHARING    // Direct file sharing link
    }

    // ---- Edge Properties ----
    private String source;          // Source node ID
    private String destination;     // Destination node ID
    private double weight;          // Connection weight (lower = faster/riskier)
    private EdgeType type;          // Type of connection
    private boolean active;         // Whether this connection is active

    // ---- Constructor ----
    public Edge(String source, String destination, double weight, EdgeType type) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.type = type;
        this.active = true;  // All connections start as active
    }

    // ---- Getters and Setters ----

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public EdgeType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // ---- Get the node on the other end of this edge ----
    public String getOtherNode(String nodeId) {
        if (source.equals(nodeId)) {
            return destination;
        } else if (destination.equals(nodeId)) {
            return source;
        }
        return null;  // nodeId is not part of this edge
    }

    // ---- String representation for debugging ----
    @Override
    public String toString() {
        String status = active ? "ACTIVE" : "BLOCKED";
        return String.format("%s <--[%s, w=%.1f]--> %s (%s)",
                source, type, weight, destination, status);
    }
}
