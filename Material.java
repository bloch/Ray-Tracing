package com.company.RayTracing;

/** This class represents a material description in the scene. */
public class Material {
    RGB diffuse;
    RGB specular;
    double phong_coef;
    RGB reflection;
    double transparency;

    double ambient_coef;

    public Material(double dr, double dg, double db, double sr, double sg, double sb, double rr, double rg, double rb, double phong_coef, double transparency) {
        this.diffuse = new RGB(dr, dg, db);
        this.specular = new RGB(sr, sg, sb);
        this.reflection = new RGB(rr, rg, rb);
        this.phong_coef = phong_coef;
        this.transparency = transparency;

        /** Ambient coeffienct */
        this.ambient_coef = 0.1;
    }
}