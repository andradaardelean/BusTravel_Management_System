package com.licenta.bustravel.service.utils;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Graph {
    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public Node getNodeByName(String name) {
        return nodes.stream()
            .filter(node -> node.getName()
                .equals(name))
            .findFirst()
            .orElse(null);
    }
}
