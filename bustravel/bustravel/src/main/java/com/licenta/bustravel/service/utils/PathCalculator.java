package com.licenta.bustravel.service.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PathCalculator {

    private static List<List<PathSegment>> allPaths; // Lista tuturor căilor găsite.

    public static List<List<PathSegment>> findAllPaths(Graph graph, Node source, Node destination) {
        allPaths = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        List<PathSegment> currentPath = new LinkedList<>();
        findAllPathsHelper(graph, source, destination, visited, currentPath);
        return allPaths;
    }

    private static void findAllPathsHelper(Graph graph, Node currentNode, Node destination,
                                           Set<Node> visited, List<PathSegment> currentPath) {
        visited.add(currentNode);
        if (currentNode.equals(destination)) {
            // La destinație, adăugăm un segment final fără un link ulterior.
            currentPath.add(new PathSegment(currentNode, null));
            if (isValidPath(graph, currentPath)) {
                allPaths.add(new LinkedList<>(currentPath));
            }
            currentPath.remove(currentPath.size() - 1); // Elimină segmentul adăugat.
        } else {
            for (Map.Entry<Link, Long> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                Node nextNode = adjacencyPair.getKey().getDestination();
                if (!visited.contains(nextNode)) {
                    // Adaugă segmentul curent cu link-ul către următorul nod înainte de a recursa.
                    currentPath.add(new PathSegment(currentNode, adjacencyPair.getKey()));
                    findAllPathsHelper(graph, nextNode, destination, visited, currentPath);
                    currentPath.remove(currentPath.size() - 1); // Backtrack: elimină ultimul segment adăugat.
                }
            }
        }
        visited.remove(currentNode);
    }

    private static boolean isValidPath(Graph graph, List<PathSegment> path) {
        // Verifică validitatea fiecărui segment.
        for (int i = 0; i < path.size() - 1; i++) {
            Link link = path.get(i).getLink();  // Verifică legătura fiecărui segment.
            Node currentNode = path.get(i).getNode();
            Node nextNode = path.get(i + 1).getNode();
            if (link == null || !link.getDestination().equals(nextNode)) {
                return false; // Link-ul nu corespunde cu nodul următor.
            }
        }
        return true;
    }

    public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
        source.setDistance(Long.valueOf(0));
        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node current = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(current);
            for (Map.Entry<Link, Long> adjacencyPair : current.getAdjacentNodes()
                .entrySet()) {
                Node adjacentNode = adjacencyPair.getKey().destination;
                Long edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, current);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(current);
        }
        return graph;
    }

    private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        long lowestDistance = Long.MAX_VALUE;
        for (Node node : unsettledNodes) {
            long nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(Node evaluationNode, Long edgeWeigh, Node sourceNode) {
        Long sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
