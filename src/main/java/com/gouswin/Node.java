package com.gouswin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class Node {

    private final String name;
    private Coordinate coordinate;

    public Node(String name, Coordinate coordinate) {
        this.name = name;
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node) obj;
            return node.name.equals(this.name);
        } else if (obj instanceof String) {
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
