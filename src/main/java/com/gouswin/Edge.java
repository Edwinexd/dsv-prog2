package com.gouswin;

public class Edge<T> {

    private int weight;

    private final T destination;

    private String name;

    public Edge(T destination, int weight, String name) {
        this.destination = destination;
        this.weight = weight;
        this.name = name;
    }

    public T getDestination() {
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

    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public String toString() {
        return "%s to %s;\n".formatted(name, destination.toString());
    }
}
