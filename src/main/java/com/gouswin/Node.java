package com.gouswin;

import javafx.scene.Cursor;
import javafx.scene.shape.Circle;

/*
    Erik Lind Gou-Said - erli1872
    Edwin Sundberg - edsu8469
*/
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
