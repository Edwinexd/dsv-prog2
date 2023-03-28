package com.gouswin;

import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class ListGraph {

    private HashSet<Node> nodes = new HashSet<>();

    public boolean add(Node node) {
        return nodes.add(node);
    }

    public void remove(Node node) throws NoSuchElementException {

        boolean res = nodes.remove(node);

        if (!res) {
            throw new NoSuchElementException("Node not found");
        }

    }


    public void connect(Node from, Node to, int weight, String name, String travelType) throws IllegalArgumentException, IllegalStateException, NoSuchElementException {
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
        from.addConnection(new Edge(name, from, to, weight, travelType));


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
        from.getConnection(to).setWeight(newWeight);

    }

    public HashSet<Node> getNodes() {
        return (HashSet<Node>) nodes.clone();
    }

    public HashSet<Edge> getEdgesfrom(Node node) throws NoSuchElementException {
        if (!nodes.contains(node)) {
            throw new NoSuchElementException("Node not found");
        }
        return node.getConnections();
    }

    public HashSet<Edge> getEdgesBetween(Node from, Node to) throws NoSuchElementException {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }
        HashSet<Edge> res = (HashSet<Edge>) from.getConnections().stream().filter(
                        edge -> edge
                                .getDestination()
                                .equals(to))
                .collect(Collectors.toSet());

        return res.isEmpty() ? null : res;
    }

    @Override
    public String toString() {
        String res = "";
        for (Node node : nodes) {
            res += node.getName() + ":\n";
            for (Edge edge : node.getConnections()) {
                res += "\t" + edge.toString() + "\n";
            }
        }
        return res;
    }


    public boolean pathExists(Node from, Node to) throws NoSuchElementException {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }
        return !runDjikstra(from, to).isEmpty();

    }

    public ArrayList<Node> getPath(Node from, Node to) throws NoSuchElementException {

        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }

        ArrayList<Node> res = runDjikstra(from, to);
        return res;

    }

    public ArrayList<Node> runDjikstra(Node start, Node target) {
        ArrayList<Pair<Integer, ArrayList<Node>>> finallist = new ArrayList<>();
        Stack<Node> nodepath = new Stack<Node>();
        HashSet visitednodes = new HashSet<Node>();
        HashSet<Node> unvisitednodes = (HashSet<Node>) nodes.clone();
        int distance = 0;
        nodepath.push(start);
        Node currentnode = start;
        if (currentnode.getConnections().isEmpty())
            return null;
        for (Edge conn : currentnode.getConnections()) {
            djikstra(distance + conn.getWeight(), conn.getDestination(), (Stack<Node>) nodepath.clone(), target, finallist);
        }
        if (finallist.isEmpty()) {
            return null;
        } else {
            finallist.sort(Comparator.comparingInt(pair -> pair.getKey()));
        }
        return finallist.get(0).getValue();

        /*
        while(true)
        {
            visitednodes.add(currentnode);
            unvisitednodes.remove(currentnode);
            HashSet<Edge> targetsSet = currentnode.getConnections();
            ArrayList<Edge> targets = (ArrayList<Edge>) targetsSet.stream()
                    .filter(edge -> !visitednodes.contains(edge.getDestination()))
                    .collect(Collectors.toList());
            targets.sort(Comparator.comparingInt(edge -> edge.getWeight()));
            if(targets.isEmpty())
            {
                Node popped = nodepath.pop();
                visitednodes.add(currentnode);
                unvisitednodes.remove(currentnode);
                distance -= popped.getConnection(currentnode).getWeight();

                if (nodepath.isEmpty())
                    return null;
                if(nodepath.peek().equals(start))
                {
                    visitednodes.clear();
                    visitednodes.add(popped);
                    unvisitednodes = (HashSet<Node>) nodes.clone();

                }
                currentnode = nodepath.peek();
            }
            else
            {
                currentnode = targets.get(0).getDestination(); // zeroth index contains the
                nodepath.push(currentnode);
                distance += targets.get(0).getWeight();
                if(currentnode.equals(target))
                {
                     return (ArrayList<Node>) nodepath.stream().collect(Collectors.toList());
                }

           }
        }
         */

    }

    public void djikstra(int currentDistance, Node currentnode, Stack<Node> nodepath, Node target, ArrayList<Pair<Integer, ArrayList<Node>>> finallist) {
        nodepath.push(currentnode);
        if (currentnode.equals(target)) {
            finallist.add(new Pair<>(currentDistance, (ArrayList<Node>) nodepath.stream().toList()));
            return;
        }

        HashSet<Edge> targetsSet = currentnode.getConnections();
        ArrayList<Edge> targets = (ArrayList<Edge>) targetsSet.stream()
                .filter(edge -> !nodepath.contains(edge.getDestination()))
                .collect(Collectors.toList());
        if (targets.isEmpty()) {
            return;
        }
        for (Edge conn : targets) {
            djikstra(currentDistance + conn.getWeight(), conn.getDestination(), (Stack<Node>) nodepath.clone(), target, finallist);

        }

    }


}

