package com.company.RayTracing;

/** This class represents a light source object in the scene. */
public class Light {
    Point position;
    RGB color;
    double specular_intensity;
    double shadow_intensity;
    double light_radius;

    public Light(double pos_x, double pos_y, double pos_z, double R, double G, double B, double specular_intensity, double shadow_intensity, double light_radius) {
        this.position = new Point(pos_x, pos_y, pos_z);
        this.color = new RGB(R, G, B);
        this.specular_intensity = specular_intensity;
        this.shadow_intensity = shadow_intensity;
        this.light_radius = light_radius;
    }

}