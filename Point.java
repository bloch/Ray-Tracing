package com.company.RayTracing;

/** This class represents a 3D point, using (x,y,z) coordinates. */
public class Point {
    public double x;
    public double y;
    public double z;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** An in-place function that adds some quantity of vector V to a point. */
    public void addVector(Vector v, double i) {
        this.x += i*v.x;
        this.y += i*v.y;
        this.z += i*v.z;
    }

    /** A function that adds some quantity of vector V to a point, and returns new point object. */
    public Point addVector2(Vector v, double i) {
        return new Point(this.x + i*v.x, this.y + i*v.y, this.z + i*v.z);
    }

    public double dist(Point v) {
        double dist = Math.sqrt((this.x - v.x)*(this.x - v.x) + (this.y - v.y)*(this.y - v.y) + (this.z - v.z)*(this.z - v.z));
        return dist;
    }
}