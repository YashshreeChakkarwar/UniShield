package com.unishield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * ============================================================
 * RISK ANALYZER - DFS-based Critical Node Detection & Risk Scoring
 * ============================================================
 * Uses Depth-First Search (DFS) to identify important nodes
 * in the network. Calculates risk scores based on multiple
 * factors: connectivity, proximity to critical servers,
 * access attempts, and past attack patterns.
 */
public class RiskAnalyzer {

    private Graph graph;
    private HashMap<String, Integer> riskScores;    // Stores calculated risk scores

    // ---- Constructor ----
    public RiskAnalyzer(Graph graph) {
        this.graph = graph;
        this.riskScores = new HashMap<>();
    }

    // ============================================================
    // FIND CRITICAL NODES - DFS-based important node detection
    // ============================================================
    // Finds nodes that are critical based on high connectivity
    // or being on paths between many other nodes (simplified
    // articulation point logic)
    public ArrayList<Node> findCriticalNodes() {
        ArrayList<Node> criticalNodes = new ArrayList<>();
        ArrayList<Node> allNodes = graph.getAllNodes();

        // Step 1: Add all critical servers first
        for (int i = 0; i < allNodes.size(); i++) {
            Node node = allNodes.get(i);
            if (node.isCritical()) {
                criticalNodes.add(node);
            }
        }

        // Step 2: Use DFS to find highly connected nodes
        // A node is considered critical if:
        //   - It has more connections than average
        //   - It's on the path to a critical server
        //   - Removing it would disconnect parts of the graph

        // Calculate average connection count
        int totalConnections = 0;
        for (int i = 0; i < allNodes.size(); i++) {
            totalConnections += graph.getConnectionCount(allNodes.get(i).getId());
        }
        double avgConnections = (double) totalConnections / allNodes.size();

        // Step 3: Run DFS-based reachability check to find bridge nodes
        for (int i = 0; i < allNodes.size(); i++) {
            Node node = allNodes.get(i);

            // Skip if already marked as critical
            if (node.isCritical()) continue;

            int connections = graph.getConnectionCount(node.getId());

            // High connectivity check
            if (connections > avgConnections + 1) {
                if (!criticalNodes.contains(node)) {
                    criticalNodes.add(node);
                }
                continue;
            }

            // Bridge node check: would removing this disconnect critical nodes?
            if (isBridgeNode(node.getId())) {
                if (!criticalNodes.contains(node)) {
                    criticalNodes.add(node);
                }
            }
        }

        return criticalNodes;
    }

    // ============================================================
    // DFS - Manual Depth-First Search implementation
    // ============================================================
    // Performs DFS from startId and returns all reachable nodes
    public ArrayList<String> performDFS(String startId) {
        ArrayList<String> visitOrder = new ArrayList<>();
        HashSet<String> visited = new HashSet<>();

        // Call recursive DFS helper
        dfsHelper(startId, visited, visitOrder);

        return visitOrder;
    }

    // ---- DFS Recursive Helper ----
    private void dfsHelper(String nodeId, HashSet<String> visited, ArrayList<String> visitOrder) {
        // Base case: already visited
        if (visited.contains(nodeId)) {
            return;
        }

        // Mark current node as visited
        visited.add(nodeId);
        visitOrder.add(nodeId);

        // Get all neighbors
        ArrayList<Node> neighbors = graph.getNeighbors(nodeId);

        // Recursively visit each unvisited neighbor
        for (int i = 0; i < neighbors.size(); i++) {
            String neighborId = neighbors.get(i).getId();
            if (!visited.contains(neighborId)) {
                dfsHelper(neighborId, visited, visitOrder);
            }
        }
    }

    // ============================================================
    // BRIDGE NODE CHECK - Would removing this node disconnect graph?
    // ============================================================
    private boolean isBridgeNode(String nodeId) {
        // Count how many nodes are reachable from any neighbor without this node
        ArrayList<Node> allNodes = graph.getAllNodes();

        if (allNodes.size() <= 2) return false;

        // Pick a neighbor to start DFS from (after "removing" nodeId)
        ArrayList<Node> neighbors = graph.getNeighbors(nodeId);
        if (neighbors.size() < 2) return false;

        String startFrom = neighbors.get(0).getId();

        // DFS without going through nodeId
        HashSet<String> visited = new HashSet<>();
        visited.add(nodeId);  // Pretend this node is removed
        dfsHelper(startFrom, visited, new ArrayList<>());

        // Check if all non-removed nodes were reached
        // If visited count (minus the "removed" node) < total nodes - 1,
        // then removing this node disconnects the graph
        int reachable = visited.size() - 1;  // Subtract the "removed" node
        int expected = allNodes.size() - 1;   // Total minus removed

        return reachable < expected;
    }

    // ============================================================
    // CALCULATE RISK SCORES - Compute risk for all nodes
    // ============================================================
    public HashMap<String, Integer> calculateRiskScores() {
        riskScores = new HashMap<>();
        ArrayList<Node> allNodes = graph.getAllNodes();

        for (int i = 0; i < allNodes.size(); i++) {
            Node node = allNodes.get(i);
            int score = calculateNodeRisk(node.getId());
            riskScores.put(node.getId(), score);
            node.setRiskScore(score);
        }

        return riskScores;
    }

    // ============================================================
    // CALCULATE NODE RISK - Compute risk score for a single node
    // ============================================================
    // Risk factors:
    //   1. Number of connections (more connections = more exposure)
    //   2. Access attempts (unusual activity)
    //   3. Proximity to critical nodes (closer = higher risk)
    //   4. Node type (critical servers inherently higher risk)
    public int calculateNodeRisk(String nodeId) {
        Node node = graph.getNode(nodeId);
        if (node == null) return 0;

        int score = 0;

        // ---- Factor 1: Connection count (max 25 points) ----
        int connections = graph.getConnectionCount(nodeId);
        int connectionScore = Math.min(connections * 8, 25);
        score += connectionScore;

        // ---- Factor 2: Access attempts (max 25 points) ----
        int accessScore = Math.min(node.getAccessAttempts() * 5, 25);
        score += accessScore;

        // ---- Factor 3: Proximity to critical nodes (max 30 points) ----
        int proximityScore = calculateProximityScore(nodeId);
        score += Math.min(proximityScore, 30);

        // ---- Factor 4: Node type bonus (max 20 points) ----
        if (node.getType() == Node.NodeType.EXAM_SERVER) {
            score += 20;  // Exam server is highest priority
        } else if (node.getType() == Node.NodeType.ATTENDANCE_SERVER) {
            score += 18;  // Attendance server is also critical
        } else if (node.getType() == Node.NodeType.LAB_SYSTEM) {
            score += 10;  // Lab systems are moderately important
        } else {
            score += 5;   // Student devices are lowest priority
        }

        // Clamp to 0-100 range
        if (score > 100) score = 100;
        if (score < 0) score = 0;

        return score;
    }

    // ---- Calculate proximity score based on distance to critical nodes ----
    private int calculateProximityScore(String nodeId) {
        // Use BFS to find shortest hop distance to any critical node
        ArrayList<Node> criticalNodes = graph.getCriticalNodes();
        if (criticalNodes.isEmpty()) return 0;

        // Check if this IS a critical node
        Node node = graph.getNode(nodeId);
        if (node != null && node.isCritical()) return 30;

        // BFS to find minimum hops to any critical node
        java.util.LinkedList<String> queue = new java.util.LinkedList<>();
        HashMap<String, Integer> hopCounts = new HashMap<>();

        queue.addLast(nodeId);
        hopCounts.put(nodeId, 0);

        int minHops = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            String current = queue.removeFirst();
            int currentHops = hopCounts.get(current);

            // Check if this is a critical node
            Node currentNode = graph.getNode(current);
            if (currentNode != null && currentNode.isCritical()) {
                if (currentHops < minHops) {
                    minHops = currentHops;
                }
                continue;  // Don't need to go further from here
            }

            // Explore neighbors
            ArrayList<Node> neighbors = graph.getNeighbors(current);
            for (int i = 0; i < neighbors.size(); i++) {
                String neighborId = neighbors.get(i).getId();
                if (!hopCounts.containsKey(neighborId)) {
                    hopCounts.put(neighborId, currentHops + 1);
                    queue.addLast(neighborId);
                }
            }
        }

        // Convert hop distance to score (closer = higher risk)
        if (minHops == Integer.MAX_VALUE) return 0;
        if (minHops == 1) return 25;
        if (minHops == 2) return 15;
        if (minHops == 3) return 8;
        return 3;
    }

    // ============================================================
    // DETECT SUSPICIOUS NODES - Nodes with abnormal behavior
    // ============================================================
    public ArrayList<Node> detectSuspiciousNodes(int connectionThreshold, int accessThreshold) {
        ArrayList<Node> suspicious = new ArrayList<>();
        ArrayList<Node> allNodes = graph.getAllNodes();

        for (int i = 0; i < allNodes.size(); i++) {
            Node node = allNodes.get(i);
            boolean isSuspicious = false;

            // Check 1: Too many connections
            int connections = graph.getConnectionCount(node.getId());
            if (connections >= connectionThreshold) {
                isSuspicious = true;
            }

            // Check 2: Too many access attempts
            if (node.getAccessAttempts() >= accessThreshold) {
                isSuspicious = true;
            }

            // Check 3: Non-authorized access to critical nodes
            if (node.isCritical() && node.getAccessAttempts() > 0) {
                isSuspicious = true;
            }

            if (isSuspicious) {
                suspicious.add(node);
            }
        }

        return suspicious;
    }

    // ============================================================
    // GET RISK SCORES MAP - Return the computed risk map
    // ============================================================
    public HashMap<String, Integer> getRiskScores() {
        return riskScores;
    }
}
