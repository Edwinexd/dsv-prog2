package com.gouswin;


import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

class PathResult
{
    public int distance;
    public ArrayList<Edge> path;
    public PathResult(int distance, ArrayList<Edge> path)
    {
        this.distance = distance;
        this.path = path;
    }

}

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

    public Node findNode(String name) {
        for (Node node : nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }


    public void connect(Node from, Node to, int weight, String travelType) throws IllegalArgumentException, IllegalStateException, NoSuchElementException {
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

    public ArrayList<Edge> getPath(Node from, Node to) throws NoSuchElementException {

        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new NoSuchElementException("One or more nodes not found");
        }

        ArrayList<Edge> res = runDjikstra(from, to);
        return res;

    }

    private ArrayList<Edge> runDjikstra(Node start, Node target) {
        ArrayList<PathResult> finallist = new ArrayList<>();
        Stack<Node> nodepath = new Stack<>();
        Stack<Edge> edgepath = new Stack<>();
        nodepath.push(start);
        Node currentnode = start;
        if (currentnode.getConnections().isEmpty())
            return null;
        for (Edge conn : currentnode.getConnections()) {
            edgepath.push(conn);
            djikstra(0 + conn.getWeight(), conn.getDestination(), (Stack<Node>) nodepath.clone(), (Stack<Edge>) edgepath.clone(), target, finallist);
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

    private void djikstra(int currentDistance, Node currentnode, Stack<Node> nodepath, Stack<Edge> edgepath, Node target, ArrayList<PathResult> finallist) {
        nodepath.push(currentnode);
        if (currentnode.equals(target)) {
            finallist.add(new PathResult(currentDistance, (ArrayList<Edge>) edgepath.stream().toList()));
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
            edgepath.push(conn);
            djikstra(currentDistance + conn.getWeight(), conn.getDestination(), (Stack<Node>) nodepath.clone(), (Stack<Edge>) edgepath.clone(), target, finallist);
            edgepath.pop();
        }

    }

    public String seraliaze() {
        StringBuilder res = new StringBuilder();
        res.append(nodes.stream().map(node -> "%s;%f;%f".formatted(node.getName(), node.getCoordinate().getX(), node.getCoordinate().getY())).collect(Collectors.joining(";")));
        res.append("\n");
        for (Node node: nodes) {
            for (Edge edge: node.getConnections()) {
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

}

