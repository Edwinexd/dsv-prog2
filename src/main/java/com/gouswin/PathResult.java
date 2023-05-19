/*
* PROG2 VT2023, Inlämningsuppgift, del 1
* Grupp 069
* Erik Lind Gou-Said - erli1872
* Edwin Sundberg - edsu8469
*/

package com.gouswin;

import java.util.List;

public class PathResult<T> {

    private final int distance;
    private final List<Edge<T>> path;

    public PathResult(List<Edge<T>> path) {
        this.path = path;
        this.distance = path.stream().mapToInt(Edge::getWeight).sum();
    }

    public int getDistance() {
        return distance;
    }

    public List<Edge<T>> getPath() {
        return path;
    }

}