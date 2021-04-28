package com.company.RayTracing;

import java.util.ArrayList;

/** This class represents the main scene parameters. */
public class Scene {
    RGB background_color;
    int num_of_shadow_rays;
    int max_recursion_depth;

    ArrayList<Material> materials;
    ArrayList<Surfaces.Sphere> spheres;
    ArrayList<Surfaces.Plane> planes;
    ArrayList<Surfaces.Box> boxes;
    ArrayList<Light> lights;

    public Scene(double R, double G, double B, int num_of_shadow_rays, int max_recursion_depth) {
        this.background_color = new RGB(R, G, B);
        this.num_of_shadow_rays = num_of_shadow_rays;
        this.max_recursion_depth = max_recursion_depth;

        this.materials = new ArrayList<>();
        this.spheres = new ArrayList<>();
        this.planes = new ArrayList<>();
        this.boxes= new ArrayList<>();
        this.lights= new ArrayList<>();
    }

    public RGB calcAVGcolor() {
        RGB color = new RGB(0,0,0);
        for(Light light : lights) {
            color.R += light.color.R;
            color.G += light.color.G;
            color.B += light.color.B;
        }
        color.R /= lights.size();
        color.G /= lights.size();
        color.B /= lights.size();

        return color;
    }


}
