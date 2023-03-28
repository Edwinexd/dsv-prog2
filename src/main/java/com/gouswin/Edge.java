package com.gouswin;

public class Edge {

    int weight;

    String name;
    Node origin;
    Node destination;

   public Edge(String name, Node origin, Node destination, int weight) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
    }

    public Node getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int newWeight) {
        weight = newWeight;
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
