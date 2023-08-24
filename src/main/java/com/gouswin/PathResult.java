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