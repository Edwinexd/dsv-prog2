package com.gouswin;

public class Edge {

    String name;
    Node origin;
    Node destination;
    double weight;

    public Node getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "name='" + name + '\'' +
                ", origin=" + origin +
                ", destination=" + destination +
                ", weight=" + weight +
                '}';
    }
}
