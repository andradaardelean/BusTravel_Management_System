package com.licenta.bustravel.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PathCalculator {

    private static List<List<Node>> allPaths;
    private final static Logger LOGGER = LoggerFactory.getLogger(PathCalculator.class.getName());
    public static List<List<Node>> findAllPaths(Graph graph, Node source, Node destination) {
        allPaths = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        List<Node> currentPath = new LinkedList<>();
        currentPath.add(source);
        findAllPathsHelper(graph, source, destination, visited, currentPath);
        return allPaths;
    }

    private static void findAllPathsHelper(Graph graph, Node currentNode, Node destination,
                                           Set<Node> visited, List<Node> currentPath) {
        visited.add(currentNode);
        if (currentNode == destination) {
            if (isValidPath(graph, currentPath)) { // Verificați dacă calea este validă
                allPaths.add(new LinkedList<>(currentPath));
            }
        } else {
            for (Map.Entry<Node, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                Node nextNode = adjacencyPair.getKey();
                if (!visited.contains(nextNode)) {
                    currentPath.add(nextNode);
                    findAllPathsHelper(graph, nextNode, destination, visited, currentPath);
                    currentPath.remove(currentPath.size() - 1); // Backtrack
                }
            }
        }
        visited.remove(currentNode);
    }

    private static boolean isValidPath(Graph graph, List<Node> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Node currentNode = path.get(i);
            Node nextNode = path.get(i + 1);
            if (!graph.getNodes().contains(currentNode) || !graph.getNodes().contains(nextNode)) {
                return false; // Dacă oricare dintre noduri nu aparține grafului, calea nu este validă
            }
            // Verifică dacă există o muchie între nodurile consecutive
            if (!currentNode.getAdjacentNodes().containsKey(nextNode)) {
                return false; // Dacă nu există o muchie între noduri, calea nu este validă
            }
        }
        return true; // Calea este validă dacă toate nodurile și muchiile sunt prezente în graf
    }

    public static Graph calculateShortestPathFromSource(Graph graph, Node source){
        source.setDistance(0);
        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while(unsettledNodes.size() != 0){
            Node current = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(current);
            for(Map.Entry< Node, Integer> adjacencyPair: current.getAdjacentNodes().entrySet()){
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if(!settledNodes.contains(adjacentNode)){
                    calculateMinimumDistance(adjacentNode, edgeWeight, current);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(current);
        }
        return graph;
    }

    private static Node getLowestDistanceNode(Set < Node > unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(Node evaluationNode,
                                                 Integer edgeWeigh, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
