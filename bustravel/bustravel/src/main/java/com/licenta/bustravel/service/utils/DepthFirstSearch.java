package com.licenta.bustravel.service.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DepthFirstSearch {

    private static List<List<PathSegment>> allPaths;

    public static List<List<PathSegment>> findAllPaths(Graph graph, Node source, Node destination) {
        allPaths = new LinkedList<>();
        Map<Node, List<Pair<LocalDateTime, Integer>>> visited = new HashMap<>();
        List<PathSegment> currentPath = new LinkedList<>();
        findAllPathsHelper(graph, source, destination, visited, currentPath, null, null);
        return allPaths;
    }

    private static void findAllPathsHelper(Graph graph, Node currentNode, Node destination,
                                           Map<Node, List<Pair<LocalDateTime, Integer>>> visitedTimes,
                                           List<PathSegment> currentPath, LocalDateTime currentTime,
                                           Integer currentRouteId) {
        // Adaugă timpul curent la lista de vizite pentru nodul curent
        visitedTimes.putIfAbsent(currentNode, new ArrayList<>());
        visitedTimes.get(currentNode)
            .add(new Pair<>(currentTime, currentRouteId));

        if (currentNode.equals(destination)) {
            if (isValidPath(currentPath)) {
                allPaths.add(new LinkedList<>(currentPath));
            }
        } else {
            for (Map.Entry<Link, Long> adjacencyPair : currentNode.getAdjacentNodes()
                .entrySet()) {
                Link link = adjacencyPair.getKey();
                Node nextNode = link.getDestination();
                LocalDateTime nextTime = link.getArrivalTime();
                Integer nextRouteId = link.getRoute()
                    .getId();
                if (currentNode.getIsTransferPoint() && !isVisitedDuring(visitedTimes, nextNode, nextTime,
                    nextRouteId)) {
                    exploreTransferOptions(graph, currentNode, destination, visitedTimes, currentPath, currentTime,
                        nextRouteId);
                } else if (!isVisitedDuring(visitedTimes, nextNode, nextTime,
                    nextRouteId) && currentTime != null && currentTime.toLocalDate()
                    .isEqual(nextTime.toLocalDate()) && currentTime.isBefore(nextTime)) {
                    currentPath.add(new PathSegment(currentNode, adjacencyPair.getKey()));
                    findAllPathsHelper(graph, nextNode, destination, visitedTimes, currentPath, nextTime, nextRouteId);
                    currentPath.remove(currentPath.size() - 1);
                } else if (!isVisitedDuring(visitedTimes, nextNode, nextTime, nextRouteId) && currentTime == null) {
                    currentPath.add(new PathSegment(currentNode, adjacencyPair.getKey()));
                    findAllPathsHelper(graph, nextNode, destination, visitedTimes, currentPath, nextTime, nextRouteId);
                    currentPath.remove(currentPath.size() - 1);
                }
            }

        }
        visitedTimes.get(currentNode)
            .remove(new Pair<>(currentTime, currentRouteId)); // Curăță vizita după explorare
    }

    private static void exploreTransferOptions(Graph graph, Node transferNode, Node destination,
                                               Map<Node, List<Pair<LocalDateTime, Integer>>> visitedTimes,
                                               List<PathSegment> currentPath, LocalDateTime arrivalTime,
                                               Integer currentRouteId) {
        // Definiți un buffer de timp minim pentru transferuri (exemplu: 15 minute).
        final Duration transferBuffer = Duration.ofMinutes(60);

        System.out.println("Exploring transfers from: " + transferNode.getStop()
            .getLocation());
        for (Map.Entry<Link, Long> adjacencyPair : transferNode.getAdjacentNodes()
            .entrySet()) {
            Link link = adjacencyPair.getKey();
            Node nextNode = link.getDestination();
            LocalDateTime nextDepartureTime = link.getDepartureTime();
            Integer nextRouteId = link.getRoute()
                .getId();

            // Verificați condițiile de timp și de rută
            if (nextRouteId.equals(currentRouteId) && nextDepartureTime.isAfter(
                arrivalTime) && nextDepartureTime.toLocalDate()
                .equals(arrivalTime.toLocalDate())) {
                System.out.println("Considering link to: " + nextNode.getStop()
                    .getLocation() + " on route: " + nextRouteId);
                if (!isVisitedDuring(visitedTimes, nextNode, nextDepartureTime, nextRouteId)) {
                    currentPath.add(new PathSegment(transferNode, link));
                    findAllPathsHelper(graph, nextNode, destination, visitedTimes, currentPath, nextDepartureTime,
                        nextRouteId);
                    currentPath.remove(currentPath.size() - 1);
                }
            }
        }
    }

    private static boolean isVisitedDuring(Map<Node, List<Pair<LocalDateTime, Integer>>> visitedTimes, Node node,
                                           LocalDateTime time, Integer routeId) {
        return visitedTimes.getOrDefault(node, Collections.emptyList())
            .stream()
            .anyMatch(pair -> Math.abs(Duration.between(pair.getLeft(), time)
                .toMinutes()) < 10 && pair.getRight()
                .equals(routeId));
    }

    private static boolean isValidPath(List<PathSegment> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Link link = path.get(i)
                .getLink();
            Node nextNode = path.get(i + 1)
                .getNode();
            if (link == null || !link.getDestination()
                .equals(nextNode)) {
                return false;
            }
        }
        return true;
    }
}
