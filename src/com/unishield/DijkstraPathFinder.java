package com.unishield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * ============================================================
 * DIJKSTRA PATH FINDER - Shortest Path Algorithm
 * ============================================================
 * Implements Dijkstra's algorithm manually to find the fastest
 * infection path from any node to critical servers.
 * Uses a PriorityQueue with custom comparator for efficiency.
 * Edge weights represent connection speed/risk.
 */
public class DijkstraPathFinder {

    private Graph graph;

    // ---- Inner class to hold path results ----
    public static class PathResult {
        public ArrayList<String> path;      // List of node IDs in the path
        public double totalWeight;          // Total path weight (distance)

        public PathResult() {
            this.path = new ArrayList<>();
            this.totalWeight = Double.MAX_VALUE;
        }

        // Format path as readable string
        public String getPathString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i));
                if (i < path.size() - 1) {
                    sb.append(" → ");
                }
            }
            return sb.toString();
        }
    }

    // ---- Inner class for priority queue entries ----
    private static class DijkstraEntry {
        String nodeId;
        double distance;

        DijkstraEntry(String nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }

    // ---- Constructor ----
    public DijkstraPathFinder(Graph graph) {
        this.graph = graph;
    }

    // ============================================================
    // FIND SHORTEST PATH - Dijkstra from source to destination
    // ============================================================
    public PathResult findShortestPath(String sourceId, String destinationId) {
        PathResult result = new PathResult();

        // Check if both nodes exist
        if (graph.getNode(sourceId) == null || graph.getNode(destinationId) == null) {
            return result;
        }

        // ---- Step 1: Initialize data structures ----
        // Distance map: stores shortest known distance to each node
        HashMap<String, Double> distances = new HashMap<>();

        // Previous map: stores the previous node in the shortest path
        HashMap<String, String> previous = new HashMap<>();

        // Visited set: tracks which nodes have been finalized
        HashMap<String, Boolean> visited = new HashMap<>();

        // Initialize all distances to infinity
        ArrayList<Node> allNodes = graph.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            String nodeId = allNodes.get(i).getId();
            distances.put(nodeId, Double.MAX_VALUE);
            previous.put(nodeId, null);
            visited.put(nodeId, false);
        }

        // Distance to source is 0
        distances.put(sourceId, 0.0);

        // ---- Step 2: Create priority queue (min-heap by distance) ----
        PriorityQueue<DijkstraEntry> priorityQueue = new PriorityQueue<>(
            new Comparator<DijkstraEntry>() {
                @Override
                public int compare(DijkstraEntry a, DijkstraEntry b) {
                    // Compare distances (ascending order)
                    if (a.distance < b.distance) return -1;
                    if (a.distance > b.distance) return 1;
                    return 0;
                }
            }
        );

        // Start with the source node
        priorityQueue.add(new DijkstraEntry(sourceId, 0.0));

        // ---- Step 3: Main Dijkstra loop ----
        while (!priorityQueue.isEmpty()) {
            // Extract node with minimum distance
            DijkstraEntry current = priorityQueue.poll();
            String currentId = current.nodeId;

            // Skip if already visited
            if (visited.get(currentId)) {
                continue;
            }

            // Mark as visited (distance is now finalized)
            visited.put(currentId, true);

            // If we reached the destination, we can stop
            if (currentId.equals(destinationId)) {
                break;
            }

            // ---- Step 4: Relax edges to neighbors ----
            ArrayList<Edge> edges = graph.getActiveEdges(currentId);
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                String neighborId = edge.getDestination();

                // Skip if the neighbor is visited or the node is protected
                Node neighborNode = graph.getNode(neighborId);
                if (visited.get(neighborId) || (neighborNode != null && neighborNode.isNodeProtected())) {
                    continue;
                }

                // Calculate new distance through current node
                double currentDist = distances.get(currentId);
                double newDistance = currentDist + edge.getWeight();

                // If this path is shorter, update the distance
                double knownDistance = distances.get(neighborId);
                if (newDistance < knownDistance) {
                    distances.put(neighborId, newDistance);
                    previous.put(neighborId, currentId);
                    // Add to priority queue with updated distance
                    priorityQueue.add(new DijkstraEntry(neighborId, newDistance));
                }
            }
        }

        // ---- Step 5: Reconstruct the path ----
        double destDistance = distances.get(destinationId);
        if (destDistance == Double.MAX_VALUE) {
            // No path exists
            return result;
        }

        result.totalWeight = destDistance;

        // Walk backwards from destination to source using 'previous' map
        ArrayList<String> reversePath = new ArrayList<>();
        String current = destinationId;
        while (current != null) {
            reversePath.add(current);
            current = previous.get(current);
        }

        // Reverse the path to get source -> destination order
        for (int i = reversePath.size() - 1; i >= 0; i--) {
            result.path.add(reversePath.get(i));
        }

        return result;
    }

    // ============================================================
    // FIND PATHS TO CRITICAL NODES - From a source to all servers
    // ============================================================
    public ArrayList<PathResult> findPathsToCriticalNodes(String sourceId) {
        ArrayList<PathResult> results = new ArrayList<>();
        ArrayList<Node> criticalNodes = graph.getCriticalNodes();

        for (int i = 0; i < criticalNodes.size(); i++) {
            Node criticalNode = criticalNodes.get(i);
            PathResult pathResult = findShortestPath(sourceId, criticalNode.getId());

            // Only add if a valid path was found
            if (!pathResult.path.isEmpty()) {
                results.add(pathResult);
            }
        }

        return results;
    }

    // ============================================================
    // FIND MOST VULNERABLE PATH - Shortest path to any critical node
    // ============================================================
    public PathResult findMostVulnerablePath(String sourceId) {
        ArrayList<PathResult> allPaths = findPathsToCriticalNodes(sourceId);

        if (allPaths.isEmpty()) {
            return new PathResult();
        }

        // Find the path with minimum total weight
        PathResult shortest = allPaths.get(0);
        for (int i = 1; i < allPaths.size(); i++) {
            if (allPaths.get(i).totalWeight < shortest.totalWeight) {
                shortest = allPaths.get(i);
            }
        }

        return shortest;
    }
}
