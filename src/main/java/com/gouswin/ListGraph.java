package com.gouswin;


import java.util.*;
import java.util.stream.Collectors;

class PathResult<T> {
    public int distance;
    public ArrayList<Edge<T>> path;

    public PathResult(int distance, ArrayList<Edge<T>> path) {
        this.distance = distance;
        this.path = path;
    }

}

public class ListGraph<T> { // DAMN YOU GENERICS

    private HashMap<T, HashSet<Edge<T>>> nodes = new HashMap<>(); // T should always be hashable, but it might not be completely correct.


    // public Set<Edge<T>> getEdges() {
    //     // TODO This is gonna be painful
    //     return (HashSet<Edge<T>>) edgeset.clone();
    // }


    public Set<T> getNodes() {
        return nodes.keySet();
    }


    public HashMap<T, HashSet<Edge<T>>> getNodeGraph(){
        return (HashMap<T, HashSet<Edge<T>>>) nodes.clone();
    }



    public HashSet<Edge<T>> getEdgesfrom(Node node) throws NoSuchElementException {
        if (nodes.get(node) == null) {
            throw new NoSuchElementException("Node not found");
        }
        return nodes.get(node);
    }

    public Edge<T> getEdgeBetween(T from, T to) throws NoSuchElementException {
        nodesExists(from, to);
        return nodes.get(from).stream().filter(edge -> edge.getDestination().equals(to)).findFirst().orElse(null);
    }

    public void add(T node) {
        nodes.put(node, new HashSet<>());
    }

    public void remove(T node) throws NoSuchElementException {
        // TODO Nuke shit that has relations to target node
        if (nodes.remove(node) == null) {
            throw new NoSuchElementException("Node not found");
        }
    }

    public boolean hasConnection(T from, T to) throws NoSuchElementException {
        return getEdgeBetween(from, to) != null;
    }


    public void connect(T from, T to, int weight, String name) throws IllegalArgumentException, IllegalStateException, NoSuchElementException {
        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot connect a node to itself");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        nodesExists(from, to);
        if (hasConnection(from, to)) {
            throw new IllegalStateException("Nodes are already connected");
        }
        // TODO Review @Edwin
        Edge<T> fromedge = new Edge<T>(to, weight, name);
        nodes.get(from).add(fromedge);

        Edge<T> toedge = new Edge<T>(from, weight, name);
        nodes.get(to).add(toedge);
    }

    public void disconnect(T from, T to) throws NoSuchElementException, IllegalStateException {
        nodesExists(from, to);
        if (!hasConnection(from, to)) {
            throw new IllegalStateException("Nodes are not connected");
        }
        // TODO Review @Edwin
        nodes.get(from).remove(getEdgeBetween(from, to));
        nodes.get(to).remove(getEdgeBetween(to, from));
    }


    public void setConnectionWeight(T from, T to, int newWeight) throws NoSuchElementException, IllegalArgumentException {
        nodesExists(from, to);
        if (newWeight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        if (hasConnection(from, to)) {
            throw new NoSuchElementException("Nodes are not connected");
        }
        getEdgeBetween(from, to).setWeight(newWeight);

    }

    @SafeVarargs
    private void nodesExists(T... nodes) throws NoSuchElementException {
        if (!this.nodes.keySet().containsAll(Arrays.asList(nodes))) {
            throw new NoSuchElementException("One or more nodes not found");
        }
    }


    @Override
    public String toString() {
        String res = "";
        for (T node : nodes.keySet()) {
            res += node.toString() + ":\n";
            for (Edge<T> edge : nodes.get(node)) {
                res += "\t" + edge.toString() + "\n";
            }
        }
        return res;
    }

    public boolean pathExists(T from, T to) throws NoSuchElementException {
        nodesExists(from, to);
        return !runDepthSearch(from, to).isEmpty();

    }

    public ArrayList<Edge<T>> getPath(T from, T to) throws NoSuchElementException {

        nodesExists(from, to);

        ArrayList<Edge<T>> res = runDepthSearch(from, to);
        return res;

    }

    private ArrayList<Edge<T>> runDepthSearch(T start, T target) {
        ArrayList<PathResult<T>> finallist = new ArrayList<>();
        Stack<T> nodepath = new Stack<>();
        Stack<Edge<T>> edgepath = new Stack<>();
        nodepath.push(start);
        T currentnode = start;
        if (nodes.get(currentnode).isEmpty())
            return null;
        for (Edge<T> conn : nodes.get(currentnode)) {
            edgepath.push(conn);
            depthSearch(0 + conn.getWeight(), (T) conn.getDestination(), (Stack<T>) nodepath.clone(), (Stack<Edge<T>>) edgepath.clone(), target, finallist);
            edgepath.pop();
        }
        if (finallist.isEmpty()) {
            return null;
        } else {
            finallist.sort(Comparator.comparingInt(pair -> pair.distance));
        }
        return finallist.get(0).path;

    }

    private void depthSearch(int currentDistance, T currentnode, Stack<T> nodepath, Stack<Edge<T>> edgepath, T target, ArrayList<PathResult<T>> finallist) {
        nodepath.push(currentnode);
        if (currentnode.equals(target)) {
            finallist.add(new PathResult<T>(currentDistance, (ArrayList<Edge<T>>) edgepath.stream().toList()));
            return;
        }

        HashSet<Edge<T>> targetsSet = nodes.get(currentnode);
        ArrayList<Edge<T>> targets = (ArrayList<Edge<T>>) targetsSet.stream()
                .filter(edge -> !nodepath.contains(edge.getDestination()))
                .collect(Collectors.toList());
        if (targets.isEmpty()) {
            return;
        }
        for (Edge<T> conn : targets) {
            edgepath.push(conn);
            depthSearch(currentDistance + conn.getWeight(), conn.getDestination(), (Stack<T>) nodepath.clone(), (Stack<Edge<T>>) edgepath.clone(), target, finallist);
            edgepath.pop();
        }

    }
}

