package com.unishield;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * ============================================================
 * DEFENSE ENGINE - Containment & Recommendation System
 * ============================================================
 * Generates automated defense suggestions using greedy logic,
 * applies containment actions (isolate node, block connection,
 * protect critical nodes), and computes before vs after comparison.
 */
public class DefenseEngine {

    private Graph graph;
    private MalwareSimulator simulator;

    // ---- Inner class for comparison results ----
    public static class ComparisonResult {
        public int infectedBefore;      // Nodes infected before defense
        public int infectedAfter;       // Nodes infected after defense
        public double damageReduction;  // Percentage of damage reduction
        public ArrayList<String> actionsApplied;  // List of actions taken

        public ComparisonResult() {
            this.actionsApplied = new ArrayList<>();
        }

        @Override
        public String toString() {
            return String.format("Before: %d infected | After: %d infected | Reduction: %.1f%%",
                    infectedBefore, infectedAfter, damageReduction);
        }
    }

    // ---- Constructor ----
    public DefenseEngine(Graph graph) {
        this.graph = graph;
        this.simulator = new MalwareSimulator(graph);
    }

    // ============================================================
    // GENERATE RECOMMENDATIONS - Greedy defense suggestions
    // ============================================================
    // Uses priority-based greedy logic to decide the best actions:
    //   1. Protect critical nodes first (highest priority)
    //   2. Isolate infected nodes near critical servers
    //   3. Block high-risk connections
    public ArrayList<String> generateRecommendations(
            HashSet<String> infectedNodes,
            ArrayList<Node> criticalNodes,
            String sourceNodeId) {

        ArrayList<String> recommendations = new ArrayList<>();

        // ---- Priority 1: Protect critical nodes that are not yet infected ----
        for (int i = 0; i < criticalNodes.size(); i++) {
            Node criticalNode = criticalNodes.get(i);
            if (!infectedNodes.contains(criticalNode.getId())) {
                recommendations.add("PROTECT: " + criticalNode.getName() +
                    " [" + criticalNode.getId() + "] — Critical server needs protection");
            } else {
                recommendations.add("ALERT: " + criticalNode.getName() +
                    " [" + criticalNode.getId() + "] — Critical server ALREADY INFECTED!");
            }
        }

        // ---- Priority 2: Isolate the source of infection ----
        if (sourceNodeId != null && graph.getNode(sourceNodeId) != null) {
            recommendations.add("ISOLATE: " + graph.getNode(sourceNodeId).getName() +
                " [" + sourceNodeId + "] — Source of malware, should be isolated immediately");
        }

        // ---- Priority 3: Block connections from infected to clean nodes ----
        // Greedy approach: find edges where one end is infected and other is clean
        ArrayList<Edge> allEdges = graph.getAllEdges();
        for (int i = 0; i < allEdges.size(); i++) {
            Edge edge = allEdges.get(i);
            if (!edge.isActive()) continue;

            boolean srcInfected = infectedNodes.contains(edge.getSource());
            boolean dstInfected = infectedNodes.contains(edge.getDestination());

            // If one side is infected and the other is clean, recommend blocking
            if (srcInfected && !dstInfected) {
                Node cleanNode = graph.getNode(edge.getDestination());
                recommendations.add("BLOCK: Connection " + edge.getSource() +
                    " ↔ " + edge.getDestination() + " — Prevent spread to " +
                    (cleanNode != null ? cleanNode.getName() : edge.getDestination()));
            } else if (!srcInfected && dstInfected) {
                Node cleanNode = graph.getNode(edge.getSource());
                recommendations.add("BLOCK: Connection " + edge.getSource() +
                    " ↔ " + edge.getDestination() + " — Prevent spread to " +
                    (cleanNode != null ? cleanNode.getName() : edge.getSource()));
            }
        }

        // ---- Priority 4: Isolate other highly infected nodes ----
        for (String infectedId : infectedNodes) {
            if (infectedId.equals(sourceNodeId)) continue;  // Already handled
            Node infectedNode = graph.getNode(infectedId);
            if (infectedNode != null && graph.getConnectionCount(infectedId) >= 3) {
                recommendations.add("ISOLATE: " + infectedNode.getName() +
                    " [" + infectedId + "] — Highly connected infected node");
            }
        }

        return recommendations;
    }

    // ============================================================
    // ISOLATE NODE - Deactivate all connections to/from a node
    // ============================================================
    public void isolateNode(String nodeId) {
        ArrayList<Edge> edges = graph.getEdgesFrom(nodeId);
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            edge.setActive(false);
            // Also deactivate the reverse edge
            ArrayList<Edge> reverseEdges = graph.getEdgesFrom(edge.getDestination());
            for (int j = 0; j < reverseEdges.size(); j++) {
                if (reverseEdges.get(j).getDestination().equals(nodeId)) {
                    reverseEdges.get(j).setActive(false);
                }
            }
        }
    }

    // ============================================================
    // BLOCK CONNECTION - Deactivate a specific edge
    // ============================================================
    public void blockConnection(String source, String destination) {
        graph.removeEdge(source, destination);
    }

    // ============================================================
    // PROTECT NODE - Mark a node as protected (immune to infection)
    // ============================================================
    public void protectNode(String nodeId) {
        Node node = graph.getNode(nodeId);
        if (node != null) {
            node.setNodeProtected(true);
            node.setInfected(false);  // Cure if already infected
        }
    }

    // ============================================================
    // APPLY DEFENSE - Apply recommended actions automatically
    // ============================================================
    // Uses greedy strategy:
    //   1. Protect all critical nodes
    //   2. Isolate the source
    //   3. Block frontier edges (infected <-> clean boundaries)
    public ArrayList<String> applyDefense(String sourceNodeId, HashSet<String> infectedNodes) {
        ArrayList<String> actionsApplied = new ArrayList<>();

        // Step 1: Protect critical nodes
        ArrayList<Node> criticalNodes = graph.getCriticalNodes();
        for (int i = 0; i < criticalNodes.size(); i++) {
            Node criticalNode = criticalNodes.get(i);
            protectNode(criticalNode.getId());
            actionsApplied.add("Protected " + criticalNode.getName());
        }

        // Step 2: Isolate the source node
        if (sourceNodeId != null && graph.getNode(sourceNodeId) != null) {
            isolateNode(sourceNodeId);
            actionsApplied.add("Isolated " + graph.getNode(sourceNodeId).getName());
        }

        // Step 3: Block frontier connections (greedy: block edges connecting
        //         infected to non-infected regions)
        ArrayList<Edge> allEdges = graph.getAllEdges();
        for (int i = 0; i < allEdges.size(); i++) {
            Edge edge = allEdges.get(i);
            if (!edge.isActive()) continue;

            boolean srcInfected = infectedNodes.contains(edge.getSource());
            boolean dstInfected = infectedNodes.contains(edge.getDestination());

            if (srcInfected != dstInfected) {
                blockConnection(edge.getSource(), edge.getDestination());
                actionsApplied.add("Blocked " + edge.getSource() + " ↔ " + edge.getDestination());
            }
        }

        return actionsApplied;
    }

    // ============================================================
    // COMPARE BEFORE & AFTER - Evaluate defense effectiveness
    // ============================================================
    public ComparisonResult compareBeforeAfter(String sourceNodeId) {
        ComparisonResult result = new ComparisonResult();

        // ---- "Before" simulation: count infected in current state ----
        // Save infected count from the last simulation
        MalwareSimulator beforeSim = new MalwareSimulator(graph);
        result.infectedBefore = 0;
        ArrayList<Node> allNodes = graph.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            if (allNodes.get(i).isInfected()) {
                result.infectedBefore++;
            }
        }

        // ---- Apply defense ----
        HashSet<String> currentlyInfected = new HashSet<>();
        for (int i = 0; i < allNodes.size(); i++) {
            if (allNodes.get(i).isInfected()) {
                currentlyInfected.add(allNodes.get(i).getId());
            }
        }
        result.actionsApplied = applyDefense(sourceNodeId, currentlyInfected);

        // ---- "After" count: clear infections and re-simulate ----
        // First reset infection states
        for (int i = 0; i < allNodes.size(); i++) {
            allNodes.get(i).setInfected(false);
        }

        // Re-run BFS with defenses in place
        MalwareSimulator afterSim = new MalwareSimulator(graph);
        afterSim.simulateSpread(sourceNodeId);
        result.infectedAfter = afterSim.getInfectedCount();

        // ---- Calculate damage reduction ----
        if (result.infectedBefore > 0) {
            int prevented = result.infectedBefore - result.infectedAfter;
            result.damageReduction = ((double) prevented / result.infectedBefore) * 100.0;
        } else {
            result.damageReduction = 0.0;
        }

        // Make sure reduction isn't negative
        if (result.damageReduction < 0) {
            result.damageReduction = 0.0;
        }

        return result;
    }
}
