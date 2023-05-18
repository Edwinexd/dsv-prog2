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
* PROG2 VT2023, Inlämningsuppgift, del 2
* Grupp 069
* Erik Lind Gou-Said - erli1872
* Edwin Sundberg - edsu8469
*/

package com.gouswin;

import javafx.scene.Cursor;
import javafx.scene.shape.Circle;

public class Node extends Circle {
    private static final int NODE_RADIUS = 10;

    private final String name;
    private Coordinate coordinate;
    private boolean selected = false;


    public Node(String name, Coordinate coordinate) {
        super(coordinate.getX(), coordinate.getY(), NODE_RADIUS);
        this.name = name;
        this.coordinate = coordinate;
        setCursor(Cursor.HAND);
        setFill(javafx.scene.paint.Color.BLUE);
        setId(name);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setFill(javafx.scene.paint.Color.RED);
        } else {
            setFill(javafx.scene.paint.Color.BLUE);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public String getName() {
        return name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node) obj;
            return node.name.equals(this.name);
        } else if (obj instanceof String) {
            String name = (String) obj;
            return this.name.equals(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

}
