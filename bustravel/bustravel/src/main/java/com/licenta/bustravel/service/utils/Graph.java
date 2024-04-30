package com.licenta.bustravel.service.utils;

import com.licenta.bustravel.model.StopEntity;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Graph {
    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public Node getNodeByStop(StopEntity stop) {
        return nodes.stream()
            .filter(node -> node.getStop().equals(stop))
            .findFirst()
            .orElse(null);
    }
}
