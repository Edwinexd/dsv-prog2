package com.gouswin;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class ListGraph {

    HashSet<Node> nodes;

    public boolean add(Node node) {
        return nodes.add(node);
    }

    public void remove(Node node) throws NoSuchElementException {

        boolean res = nodes.remove(node);

        if (!res) {
            throw new NoSuchElementException("Node not found");
        }

    }


    public void connect(Node from, Node to, int weight, String name) throws IllegalArgumentException, IllegalStateException, NoSuchElementException {
        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot connect a node to itself");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }
        if (from.hasConnection(to)) {
            throw new IllegalStateException("Nodes are already connected");
        }
        from.addConnection(new Edge(name, from, to, weight));


    }

    public void disconnect(Node from, Node to) throws NoSuchElementException, IllegalStateException {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }
        if (!from.hasConnection(to)) {
            throw new IllegalStateException("Nodes are not connected");
        }
        from.removeConnection(to);

    }

    public void setConnectionWeight(Node from, Node to, int newWeight) throws NoSuchElementException, IllegalArgumentException {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }
        if (newWeight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        if (!from.hasConnection(to)) {
            throw new NoSuchElementException("Nodes are not connected");
        }
        from.getConnection(to).weight = newWeight;

    }

    public HashSet<Node> getNodes() {
        return nodes;
    }

    public HashSet<Edge> getEdgesfrom(Node node) throws NoSuchElementException {
        if(!nodes.contains(node)){
            throw new NoSuchElementException("Node not found");
        }
        return node.getConnections();
    }

    public HashSet<Edge> getEdgesBetween(Node from, Node to) throws NoSuchElementException {
        if(!nodes.contains(from) || !nodes.contains(to)){
            throw new NoSuchElementException("One or more nodes not found");
        }
        HashSet<Edge> res = (HashSet<Edge>) from.getConnections().stream().filter(
                edge -> edge
                        .getDestination()
                        .equals(to))
                .collect(Collectors.toSet());

        return res.isEmpty() ? null : res;
    }

    public boolean pathExists(Node from, Node to) throws NoSuchElementException {
        if(!nodes.contains(from) || !nodes.contains(to)){
            throw new NoSuchElementException("One or more nodes not found");
        }
        return from.hasConnection(to);

    }


    public String toString() {
        return "";
    }


    public ArrayList<Edge> getPath(Node from, Node to) throws NoSuchElementException {

        if(!nodes.contains(from) || !nodes.contains(to)){
            throw new NoSuchElementException("One or more nodes not found");
        }

        runDjikstra(from, to);
        return null;

    }

    public ArrayList<Node> runDjikstra(Node start, Node target){
        HashSet visitednodes = new HashSet<Node>();
        Stack<Node> nodepath = new Stack<Node>();
        HashSet<Node> unvisitednodes = (HashSet<Node>) nodes.clone();
        int distance = 0;
        nodepath.push(start);
        Node currentnode = start;
        while(true)
        {
           HashSet<Edge> targetsSet = currentnode.getConnections();
           ArrayList<Edge> targets = (ArrayList<Edge>) targetsSet.stream()
                   .filter(edge -> !visitednodes.contains(edge.getDestination()))
                   .collect(Collectors.toList());
           targets.sort(Comparator.comparingInt(edge -> edge.getWeight()));
           if(targets.isEmpty())
           {

           }
        }

    }


}

