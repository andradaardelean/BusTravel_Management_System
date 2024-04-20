package com.licenta.bustravel.service.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
@Getter
@Setter
public class Node {
    String name;
    private List<Node> shortestPath = new LinkedList<>();
    private Integer distance = Integer.MAX_VALUE;
    private Map<Node, Integer> adjacentNodes = new HashMap<>();
    public void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public Node(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Node{" + "name='" + name + '\'' + ", shortestPath=" + shortestPath + ", distance=" + distance + ", " +
            "adjacentNodes=" + adjacentNodes + '}';
    }
}
