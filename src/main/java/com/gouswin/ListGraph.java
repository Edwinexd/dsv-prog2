package com.gouswin;


import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

class PathResult<T>
{
    public int distance;
    public ArrayList<Edge<T>> path;
    public PathResult(int distance, ArrayList<Edge<T>> path)
    {
        this.distance = distance;
        this.path = path;
    }

}

public class ListGraph<T> { // DAMN YOU GENERICS

    private HashSet<T> nodes = new HashSet<>();
    private HashMap<T,ArrayList<Edge<T>>> edges = new HashMap<>(); // T should always be hashable, but it might not be completely correct.

    public boolean add(T node) {
        return nodes.add(node);
    }

    public void remove(T node) throws NoSuchElementException {

        boolean res = nodes.remove(node);

        if (!res) {
            throw new NoSuchElementException("Node not found");
        }

    }

    public T findNode(String name) {
        for (T node : nodes) {
            if (node.equals(name)) {
                return node;
            }
        }
        return null;
    }


    public void connect(T from, T to, int weight, String travelType) throws IllegalArgumentException, IllegalStateException, NoSuchElementException {
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
        from.addConnection(new Edge(from, to, weight, travelType));


    }

    public void disconnect(T from, T to) throws NoSuchElementException, IllegalStateException {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }
        if (!from.hasConnection(to)) {
            throw new IllegalStateException("Nodes are not connected");
        }
        from.removeConnection(to);

    }

    public void setConnectionWeight(T from, T to, int newWeight) throws NoSuchElementException, IllegalArgumentException {
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

    public HashSet<T> getNodes() {
        return (HashSet<T>) nodes.clone();
    }

    public HashSet<Edge<T>> getEdgesfrom(Node node) throws NoSuchElementException {
        if (!nodes.contains(node)) {
            throw new NoSuchElementException("Node not found");
        }
        return node.getConnections();
    }

    public HashSet<Edge<T>> getEdgesBetween(T from, T to) throws NoSuchElementException {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }
        HashSet<Edge<T>> res = (HashSet<Edge<T>>) from.getConnections().stream().filter(
                        edge -> (edge
                                .getDestination()
                                .equals(to))
                .collect(Collectors.toSet()); //TODO :

        return res.isEmpty() ? null : res;
    }

    @Override
    public String toString() {
        String res = "";
        for (T node : nodes) {
            res += node.getName() + ":\n";
            for (Edge<T> edge : node.getConnections()) {
                res += "\t" + edge.toString() + "\n";
            }
        }
        return res;
    }


    public boolean pathExists(T from, T to) throws NoSuchElementException {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }
        return !runDjikstra(from, to).isEmpty();

    }

    public ArrayList<Edge<T>> getPath(T from, T to) throws NoSuchElementException {

        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }

        ArrayList<Edge<T>> res = runDepthSearch(from, to);
        return res;

    }

    private ArrayList<Edge<T>> runDepthSearch(T start, T target) {
        ArrayList<PathResult<T>> finallist = new ArrayList<>();
        Stack<T> nodepath = new Stack<>();
        Stack<Edge<T>> edgepath = new Stack<>();
        nodepath.push(start);
        T currentnode = start;
        if (currentnode.getConnections().isEmpty())
            return null;
        for (Edge conn : currentnode.getConnections()) {
            edgepath.push(conn);
            depthSearch(0 + conn.getWeight(), conn.getDestination(), (Stack<T>) nodepath.clone(), (Stack<Edge<T>>) edgepath.clone(), target, finallist);
            edgepath.pop();
        }
        if (finallist.isEmpty()) {
            return null;
        } else {
            finallist.sort(Comparator.comparingInt(pair -> pair.distance));
        }
        return finallist.get(0).path;

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

    private void depthSearch(int currentDistance, T currentnode, Stack<T> nodepath, Stack<Edge<T>> edgepath, T target, ArrayList<PathResult> finallist) {
        nodepath.push(currentnode);
        if (currentnode.equals(target)) {
            finallist.add(new PathResult(currentDistance, (ArrayList<Edge<T>>) edgepath.stream().toList()));
            return;
        }

        HashSet<Edge<T>> targetsSet = currentnode.getConnections();
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

    //TODO: These functions will have to be moved out of ListGraph

    /*
    public String seraliaze() {
        StringBuilder res = new StringBuilder();
        res.append(nodes.stream().map(node -> "%s;%f;%f".formatted(node.getName(), node.getCoordinate().getX(), node.getCoordinate().getY())).collect(Collectors.joining(";")));
        res.append("\n");
        for (Node node: nodes) {
            for (Edge<Node> edge: node.getConnections()) {
                res.append("%s;%s;%s;%d\n".formatted(node.getName(), edge.getDestination().getName(), edge.getTravelType(), edge.getWeight()));
            }
        }
        return res.toString();
    }

    public static ListGraph desterialise(String input) {
        ListGraph res = new ListGraph();
        String[] lines = input.split("\n");
        String[] nodes = lines[1].split(";");

        for (int i = 0; i < nodes.length; i+=3) {
            // TODO Dont use replace
            res.add(new Node(nodes[i], new Coordinate(Double.parseDouble(nodes[i+1].replace(",", ".")), Double.parseDouble(nodes[i+2].replace(",", ".")))));
        }
        for (int i = 2; i < lines.length; i++) {
            String[] edgeData = lines[i].split(";");
            Node from = res.findNode(edgeData[0]);
            Node to = res.findNode(edgeData[1]);
            res.connect(from, to, Integer.parseInt(edgeData[3]), edgeData[2]);
        }
        return res;


    }
     */

}

