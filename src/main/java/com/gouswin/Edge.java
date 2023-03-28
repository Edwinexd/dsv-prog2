package com.gouswin;

public class Edge {

    private int weight;

    private final String name;
    private final Node origin;
    private final Node destination;

    private String travelType;

    public Edge(Node origin, Node destination, int weight, String travelType) {
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
        this.travelType = travelType;
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
        return "%s to %s;\n".formatted(travelType, destination.getName());
    }
}
