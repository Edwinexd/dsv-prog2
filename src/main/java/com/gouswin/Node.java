package com.gouswin;

/*
    Erik Lind Gou-Said - erli1872
    Edwin Sundberg - edsu8469
*/
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
