package com.licenta.bustravel.service.utils;

public class PathSegment {
    private Node node;
    private Link link;

    public PathSegment(Node node, Link link) {
        this.node = node;
        this.link = link;
    }

    public Node getNode() {
        return node;
    }

    public Link getLink() {
        return link;
    }

    @Override
    public String toString() {
        if (link != null) {
            return "Stop: " + node.getStop().getLocation() + " -> Depart at: " + link.getDepartureTime() +
                " [Travel to: " + link.getDestination().getStop().getLocation() + " Arrive at: " + link.getArrivalTime() + "]";
        }
        return "Stop: " + node.getStop().getLocation();
    }
}
