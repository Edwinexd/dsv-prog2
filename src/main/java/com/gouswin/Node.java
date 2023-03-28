package com.gouswin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class Node {

    private final String name;
    private Coordinate coordinate;
    private HashSet<Edge> connections;

    public Node(String name, Coordinate coordinate) {
        this.name = name;
        this.coordinate = coordinate;
        this.connections = new HashSet<Edge>();
    }

    public String getName() {
        return name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void addConnection(Edge edge) {
        connections.add(edge);
    }

    public boolean hasConnection(Node target) {
        for (Edge edge : connections) {
            if (edge.getDestination().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public void removeConnection(Node target) throws NoSuchElementException {
        for (Edge edge : connections) {
            if (edge.getDestination().equals(target)) {
                connections.remove(edge);
                return;
            }
        }
        throw new NoSuchElementException("No connection found");
    }

    public Edge getConnection(Node target) throws NoSuchElementException {
        for (Edge edge : connections) {
            if (edge.getDestination().equals(target)) {
                return edge;
            }
        }
        throw new NoSuchElementException("No connection found");
    }

    public HashSet<Edge> getConnections() {
        return (HashSet<Edge>) connections.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node) obj;
            return node.name.equals(this.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

}
