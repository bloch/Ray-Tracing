package com.company.RayTracing;

/** This class represents a vector. */
public class Vector {
    public double x;
    public double y;
    public double z;

    public Vector(Point p1, Point p2, boolean normalize) {
        this.x = p1.x - p2.x;
        this.y = p1.y - p2.y;
        this.z = p1.z - p2.z;

        if(normalize) {
            this.normalize();
        }
    }
    public Vector(double x, double y, double z, boolean normalize) {
        this.x = x;
        this.y = y;
        this.z = z;

        if(normalize) {
            this.normalize();
        }
    }

    public double norm() {
        return Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
    }

    public double dot_product(Vector v) {
        return this.x*v.x + this.y*v.y + this.z*v.z;
    }

    public static Vector cross_product(Vector u, Vector v) {
        return new Vector(u.y*v.z - u.z*v.y, u.z*v.x - u.x*v.z, u.x*v.y - u.y*v.x, true);
    }

    public void normalize() {
        double norm = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.x /= norm;
        this.y /= norm;
        this.z /= norm;
    }

    public static Vector leftMult(Vector v, double[][] M) {
        return new Vector(v.x*M[0][0] + v.y*M[1][0] + v.z * M[2][0],
                          v.x*M[0][1] + v.y*M[1][1] + v.z * M[2][1],
                          v.x*M[0][2] + v.y*M[1][2] + v.z * M[2][2], true);
    }
}
