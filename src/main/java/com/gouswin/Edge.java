package com.gouswin;

public class Edge<T> {

    private int weight;

    private final String name;
    private final T origin;
    private final T destination;

    private String travelType;

    public Edge(String name, T origin, T destination, int weight, String travelType) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
        this.travelType = travelType;
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

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String newTravelType) {
        this.travelType = newTravelType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "%s to %s;\n".formatted(travelType, destination.toString());
    }
}
