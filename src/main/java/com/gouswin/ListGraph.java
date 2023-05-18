/* 
PathFinder - A program to visualize locations on a map and allow for pathfinding between them.
Copyright (C) 2023 Edwin Sundberg and Erik Lind Gou-Said

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/*
* PROG2 VT2023, Inlämningsuppgift, del 1
* Grupp 069
* Erik Lind Gou-Said - erli1872
* Edwin Sundberg - edsu8469
*/

package com.gouswin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class ListGraph<T> implements Graph<T> {

    private HashMap<T, HashSet<Edge<T>>> nodes = new HashMap<>();

    public Set<T> getNodes() {
        return nodes.keySet();
    }
    
    public HashSet<Edge<T>> getEdgesFrom(T node) throws NoSuchElementException {
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
        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException("Node not found");
        }

        List<T> destNode = new ArrayList<>();

        for (Edge<T> edge : nodes.get(node)) {
            destNode.add(edge.getDestination());

        }

        destNode.forEach(dest -> disconnect(node, dest));

        nodes.get(node).clear();

        nodes.remove(node);
    }

    public boolean hasConnection(T from, T to) throws NoSuchElementException {
        return getEdgeBetween(from, to) != null;
    }

    public void connect(T from, T to, String name, int weight)
            throws IllegalArgumentException, IllegalStateException, NoSuchElementException {
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

        nodes.get(from).remove(getEdgeBetween(from, to));
        nodes.get(to).remove(getEdgeBetween(to, from));
    }

    public void setConnectionWeight(T from, T to, int newWeight)
            throws NoSuchElementException, IllegalArgumentException {
        nodesExists(from, to);
        if (newWeight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        if (!hasConnection(from, to)) {
            throw new NoSuchElementException("Nodes are not connected");
        }
        getEdgeBetween(from, to).setWeight(newWeight);
        getEdgeBetween(to, from).setWeight(newWeight);

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

    public boolean pathExists(T from, T to) {
        if (!nodes.containsKey(from) || !nodes.containsKey(to)) {
            return false;
        }
        return runDepthSearch(from, to) != null;

    }

    public List<Edge<T>> getPath(T from, T to) throws NoSuchElementException {

        nodesExists(from, to);

        List<Edge<T>> res = runDepthSearch(from, to);
        return res;

    }

    private List<Edge<T>> runDepthSearch(T start, T target) {
        List<PathResult<T>> finallist = new LinkedList<>();
        Stack<T> nodepath = new Stack<>();
        Stack<Edge<T>> edgepath = new Stack<>();
        nodepath.push(start);
        T currentnode = start;
        if (nodes.get(currentnode).isEmpty())
            return null;
        for (Edge<T> conn : nodes.get(currentnode)) {
            edgepath.push(conn);
            depthSearch(conn.getDestination(), nodepath, edgepath, target, finallist);
            edgepath.pop();
        }
        if (finallist.isEmpty()) {
            return null;
        } else {
            finallist.sort(Comparator.comparingInt(pair -> pair.getDistance()));
        }
        return finallist.isEmpty() ? null : finallist.get(0).getPath();

    }

    private void depthSearch(T currentnode, Stack<T> nodepath, Stack<Edge<T>> edgepath, T target,
            List<PathResult<T>> finallist) {
        nodepath.push(currentnode);
        if (currentnode.equals(target)) {
            finallist.add(new PathResult<T>(edgepath.stream().toList()));
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
            depthSearch(conn.getDestination(), nodepath, edgepath, target, finallist);
            edgepath.pop();
        }

    }
}
