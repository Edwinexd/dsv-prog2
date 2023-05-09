package com.gouswin;
/*
    Erik Lind Gou-Said - erli1872
    Edwin Sundberg - edsu
 */
public class Coordinate {
    private double x;
    private double y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "<Coordinate x=%.5f, y=%.5f>".formatted(x, y);
    }
}
