package com.unishield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * ============================================================
 * GRAPH CLASS - University Network Graph (Adjacency List)
 * ============================================================
 * Implements the network graph using an adjacency list structure.
 * Uses HashMap<String, ArrayList<Edge>> for efficient neighbor lookup.
 * Supports dynamic modification (add/remove nodes and edges).
 */
public class Graph {

    // ---- Data Structures ----
    // Map of node ID -> Node object (stores all nodes)
    private HashMap<String, Node> nodes;

    // Adjacency list: node ID -> list of edges from that node
    private HashMap<String, ArrayList<Edge>> adjacencyList;

    // ---- Constructor ----
    public Graph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    // ============================================================
    // ADD NODE - Add a new node to the graph
    // ============================================================
    public void addNode(Node node) {
        // Only add if the node doesn't already exist
        if (!nodes.containsKey(node.getId())) {
            nodes.put(node.getId(), node);
            // Initialize an empty edge list for this node
            adjacencyList.put(node.getId(), new ArrayList<>());
        }
    }

    // ============================================================
    // ADD EDGE - Add a connection between two nodes (undirected)
    // ============================================================
    public void addEdge(Edge edge) {
        String src = edge.getSource();
        String dst = edge.getDestination();

        // Make sure both nodes exist in the graph
        if (!nodes.containsKey(src) || !nodes.containsKey(dst)) {
            return;  // Cannot add edge if nodes don't exist
        }

        // Add edge to source's adjacency list
        ArrayList<Edge> srcEdges = adjacencyList.get(src);
        srcEdges.add(edge);

        // Add reverse edge to destination's adjacency list (undirected graph)
        Edge reverseEdge = new Edge(dst, src, edge.getWeight(), edge.getType());
        ArrayList<Edge> dstEdges = adjacencyList.get(dst);
        dstEdges.add(reverseEdge);
    }

    // ============================================================
    // REMOVE NODE - Remove a node and all its connections
    // ============================================================
    public void removeNode(String nodeId) {
        if (!nodes.containsKey(nodeId)) {
            return;  // Node doesn't exist
        }

        // Step 1: Remove all edges that connect to this node
        // Go through every other node's edge list and remove edges to this node
        Set<String> allNodeIds = adjacencyList.keySet();
        for (String otherId : allNodeIds) {
            if (!otherId.equals(nodeId)) {
                ArrayList<Edge> otherEdges = adjacencyList.get(otherId);
                // Manually iterate and remove matching edges
                ArrayList<Edge> toKeep = new ArrayList<>();
                for (int i = 0; i < otherEdges.size(); i++) {
                    Edge e = otherEdges.get(i);
                    if (!e.getDestination().equals(nodeId)) {
                        toKeep.add(e);
                    }
                }
                adjacencyList.put(otherId, toKeep);
            }
        }

        // Step 2: Remove this node's edge list
        adjacencyList.remove(nodeId);

        // Step 3: Remove the node itself
        nodes.remove(nodeId);
    }

    // ============================================================
    // REMOVE EDGE - Remove a specific connection (deactivate it)
    // ============================================================
    public void removeEdge(String source, String destination) {
        // Deactivate edges in both directions (undirected graph)
        deactivateEdge(source, destination);
        deactivateEdge(destination, source);
    }

    // Helper: Deactivate a specific directional edge
    private void deactivateEdge(String from, String to) {
        ArrayList<Edge> edges = adjacencyList.get(from);
        if (edges == null) return;

        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (e.getDestination().equals(to)) {
                e.setActive(false);  // Mark as blocked, don't delete
            }
        }
    }

    // ============================================================
    // REACTIVATE EDGE - Restore a previously blocked connection
    // ============================================================
    public void reactivateEdge(String source, String destination) {
        reactivateDirectionalEdge(source, destination);
        reactivateDirectionalEdge(destination, source);
    }

    private void reactivateDirectionalEdge(String from, String to) {
        ArrayList<Edge> edges = adjacencyList.get(from);
        if (edges == null) return;

        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (e.getDestination().equals(to)) {
                e.setActive(true);
            }
        }
    }

    // ============================================================
    // GET NEIGHBORS - Get all connected nodes (active edges only)
    // ============================================================
    public ArrayList<Node> getNeighbors(String nodeId) {
        ArrayList<Node> neighbors = new ArrayList<>();
        ArrayList<Edge> edges = adjacencyList.get(nodeId);

        if (edges == null) return neighbors;

        // Walk through the edge list and collect neighbors
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            // Only include active connections
            if (edge.isActive()) {
                String neighborId = edge.getDestination();
                Node neighbor = nodes.get(neighborId);
                if (neighbor != null) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    // ============================================================
    // GET ACTIVE EDGES - Get all active edges from a node
    // ============================================================
    public ArrayList<Edge> getActiveEdges(String nodeId) {
        ArrayList<Edge> activeEdges = new ArrayList<>();
        ArrayList<Edge> allEdges = adjacencyList.get(nodeId);

        if (allEdges == null) return activeEdges;

        for (int i = 0; i < allEdges.size(); i++) {
            Edge edge = allEdges.get(i);
            if (edge.isActive()) {
                activeEdges.add(edge);
            }
        }
        return activeEdges;
    }

    // ============================================================
    // GET ALL EDGES - Get every edge in the graph (one direction only)
    // ============================================================
    public ArrayList<Edge> getAllEdges() {
        ArrayList<Edge> allEdges = new ArrayList<>();

        // To avoid duplicates in undirected graph, only add edge
        // where source ID is lexicographically less than destination
        for (String nodeId : adjacencyList.keySet()) {
            ArrayList<Edge> edges = adjacencyList.get(nodeId);
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                if (edge.getSource().compareTo(edge.getDestination()) < 0) {
                    allEdges.add(edge);
                }
            }
        }
        return allEdges;
    }

    // ============================================================
    // GET CONNECTION COUNT - Number of active connections for a node
    // ============================================================
    public int getConnectionCount(String nodeId) {
        ArrayList<Edge> edges = adjacencyList.get(nodeId);
        if (edges == null) return 0;

        int count = 0;
        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).isActive()) {
                count++;
            }
        }
        return count;
    }

    // ============================================================
    // GETTERS - Access graph data
    // ============================================================

    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public ArrayList<Node> getAllNodes() {
        ArrayList<Node> nodeList = new ArrayList<>();
        for (Node node : nodes.values()) {
            nodeList.add(node);
        }
        return nodeList;
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public boolean hasNode(String nodeId) {
        return nodes.containsKey(nodeId);
    }

    // Get all edges from a specific node (including inactive)
    public ArrayList<Edge> getEdgesFrom(String nodeId) {
        ArrayList<Edge> edges = adjacencyList.get(nodeId);
        if (edges == null) return new ArrayList<>();
        return edges;
    }

    // ============================================================
    // RESET - Reset all nodes and edges to original state
    // ============================================================
    public void resetAll() {
        // Reset all nodes
        for (Node node : nodes.values()) {
            node.reset();
        }
        // Reactivate all edges
        for (String nodeId : adjacencyList.keySet()) {
            ArrayList<Edge> edges = adjacencyList.get(nodeId);
            for (int i = 0; i < edges.size(); i++) {
                edges.get(i).setActive(true);
            }
        }
    }

    // ============================================================
    // GET CRITICAL NODES - Return nodes marked as critical servers
    // ============================================================
    public ArrayList<Node> getCriticalNodes() {
        ArrayList<Node> critical = new ArrayList<>();
        for (Node node : nodes.values()) {
            if (node.isCritical()) {
                critical.add(node);
            }
        }
        return critical;
    }
}
