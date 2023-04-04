package com.gouswin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class Node {

    private final String name;
    private Coordinate coordinate;
    private HashSet<Edge<Node>> connections;

    public Node(String name, Coordinate coordinate) {
        this.name = name;
        this.coordinate = coordinate;
        this.connections = new HashSet<Edge<Node>>();
    }

    public String getName() {
        return name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void addConnection(Edge<Node> edge) {
        connections.add(edge);
    }

    public boolean hasConnection(Node target) {
        for (Edge<Node> edge : connections) {
            if (edge.getDestination().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public void removeConnection(Node target) throws NoSuchElementException {
        for (Edge<Node> edge : connections) {
            if (edge.getDestination().equals(target)) {
                connections.remove(edge);
                return;
            }
        }
        throw new NoSuchElementException("No connection found");
    }

    public Edge<Node> getConnection(Node target) throws NoSuchElementException {
        for (Edge<Node> edge : connections) {
            if (edge.getDestination().equals(target)) {
                return edge;
            }
        }
        throw new NoSuchElementException("No connection found");
    }

    public HashSet<Edge<Node>> getConnections() {
        return (HashSet<Edge<Node>>) connections.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node) obj;
            return node.name.equals(this.name);
        }
        else if (obj instanceof String)
        {
            String name = (String) obj;
            return this.name.equals(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

}
