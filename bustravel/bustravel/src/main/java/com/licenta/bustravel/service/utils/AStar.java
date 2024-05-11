package com.licenta.bustravel.service.utils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class AStar {

    public static List<List<PathSegment>> calculateAllShortestPaths(Graph graph, Node source, Node destination) {
        source.setDistance(0L);
        Map<Node, List<List<PathSegment>>> allPaths = new HashMap<>();
        allPaths.put(source, Collections.singletonList(new LinkedList<>())); // Start with the source node having an empty path

        // Priority queue now uses the actual distance and the heuristic estimate
        PriorityQueue<Node> unsettledNodes = new PriorityQueue<>(Comparator.comparingLong(n -> n.getDistance() + heuristic(n, destination)));

        unsettledNodes.add(source);

        while (!unsettledNodes.isEmpty()) {
            Node current = unsettledNodes.poll();

            if (current.equals(destination)) {
                return allPaths.getOrDefault(destination, Collections.emptyList());  // return all paths found to the destination
            }

            for (Map.Entry<Link, Long> adjacencyPair : current.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey().getDestination();
                Long edgeWeight = adjacencyPair.getValue();
                LocalDateTime edgeArrivalTime = adjacencyPair.getKey().getArrivalTime();
                Integer routeId = adjacencyPair.getKey().getRoute().getId();

                Long newDistance = current.getDistance() + edgeWeight;
                List<List<PathSegment>> currentPaths = allPaths.get(current);

                if (!allPaths.containsKey(adjacentNode) || adjacentNode.getDistance() > newDistance) {
                    List<List<PathSegment>> newPaths = new LinkedList<>();
                    for (List<PathSegment> path : currentPaths) {
                        if (isValidToExtend(path, edgeArrivalTime, routeId)) {
                            List<PathSegment> newPath = new LinkedList<>(path);
                            newPath.add(new PathSegment(current, adjacencyPair.getKey()));
                            newPaths.add(newPath);
                        }
                    }
                    if (!newPaths.isEmpty()) {
                        adjacentNode.setDistance(newDistance);
                        allPaths.put(adjacentNode, newPaths);
                        unsettledNodes.add(adjacentNode);
                    }
                }
            }
        }
        return Collections.emptyList();  // return empty if destination is not reachable
    }

    private static boolean isValidToExtend(List<PathSegment> path, LocalDateTime arrivalTime, Integer routeId) {
        if (path.isEmpty()) return true;
        PathSegment lastSegment = path.get(path.size() - 1);
        LocalDateTime lastTime = lastSegment.getLink().getArrivalTime();
        Integer lastRouteId = lastSegment.getLink().getRoute().getId();
        return lastRouteId.equals(routeId) && lastTime.toLocalDate().equals(arrivalTime.toLocalDate()) && lastTime.isBefore(arrivalTime);
    }

    private static long heuristic(Node current, Node destination) {
        try {
            String responseData = DistanceMatrix.getData(current.getStop().getLocation(), destination.getStop().getLocation());
            Map<String, String> distanceData = DistanceMatrix.parseData(responseData);
            // Get the distance in meters from the API response
            return Long.parseLong(distanceData.get("distanceValue"));
        } catch (Exception e) {
            // In case of error, fall back to a default heuristic or throw an exception
            return Long.MAX_VALUE;
        }
    }
}