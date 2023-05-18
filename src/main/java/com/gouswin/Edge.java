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

public class Edge<T> {

    private int weight;

    private final T destination;

    private String name;

    public Edge(T destination, int weight, String name) {
        this.destination = destination;
        this.weight = weight;
        this.name = name;
    }

    public T getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int newWeight) {
        weight = newWeight;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public String toString() {
        return "till %s med %s tar %d\n".formatted(destination, name, weight);
    }
}
