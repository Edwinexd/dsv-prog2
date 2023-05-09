package com.gouswin;

import java.util.List;

/*
    Erik Lind Gou-Said - erli1872
    Edwin Sundberg - edsu8469
 */
public class PathResult<T> {

    private int distance;
    private List<Edge<T>> path;

    public PathResult(List<Edge<T>> path) {
        this.distance = 0;
        this.path = path;

        for (Edge<T> e : path) {
            distance += e.getWeight();
        }

    }

    public int getDistance() {
        return distance;
    }

    public List<Edge<T>> getPath() {
        return path;
    }

}