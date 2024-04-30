package com.licenta.bustravel.service.utils;

import com.licenta.bustravel.model.StopEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
@Getter
@Setter
public class Node {
    StopEntity stop;
    private List<Node> shortestPath = new LinkedList<>();
    private Long distance = Long.MAX_VALUE;
    private Map<Link, Long> adjacentNodes = new HashMap<>();
    public void addDestination(Link link, long distance) {
        adjacentNodes.put(link, distance);
    }

    public Node(StopEntity stop) {
        this.stop = stop;
    }

    public Link getFromAdjacentNodes(Link link) {
        return adjacentNodes.entrySet().stream()
            .filter(entry -> entry.getKey().equals(link))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }


    @Override
    public String toString() {
        return "Node{" + "name='" + stop.getLocation() + '\'' + ", shortestPath=" + shortestPath + ", distance=" + distance + ", " +
            "adjacentNodes=" + adjacentNodes + '}';
    }
}
