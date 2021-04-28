package com.company.RayTracing;

import java.util.ArrayList;

/** This class includes all sub-classes of different objects in the scene. */
public class Surfaces {
    public int mat_index;

    public static class Sphere extends Surfaces {
            public Point center;
            public double radius;

            public Sphere(double x, double y, double z, double radius, int mat_index) {
                this.center = new Point(x,y,z);
                this.radius = radius;
                this.mat_index = mat_index;
            }
    }

    public static class Plane extends Surfaces {
            Point N;
            double offset;

            public Plane(double x, double y, double z, double offset, int mat_index) {
                this.N = new Point(x,y,z);
                this.offset = offset;
                this.mat_index = mat_index;
            }
    }

    public static class Box extends Surfaces {
        public Point center;
        public double scale;

        public Box(double x_center, double y_center, double z_center, double scale, int mat_index) {
            this.center = new Point(x_center, y_center, z_center);
            this.scale = scale;
            this.mat_index = mat_index;
        }
    }

    /** Calculates the normal to the surface, or null if no surface was passed as parameter. **/
    public static Vector calcNormalToSurface(Intersection intersection, Point camera_position, Vector V) {
        Point p_intersection = camera_position.addVector2(V, intersection.t);
        if(intersection.obj instanceof Surfaces.Sphere) {
            Surfaces.Sphere sphere = (Surfaces.Sphere)intersection.obj;
            return new Vector(p_intersection, sphere.center, true);
        }
        if(intersection.obj instanceof Surfaces.Plane) {
            Surfaces.Plane plane = (Surfaces.Plane)intersection.obj;
            return new Vector(plane.N.x, plane.N.y, plane.N.z, true);
        }
        if(intersection.obj instanceof Surfaces.Box) {
            Surfaces.Box box = (Surfaces.Box) intersection.obj;

            ArrayList<Plane> planes = new ArrayList<>();
            planes.add(new Surfaces.Plane(1, 0, 0, box.center.x - 0.5*box.scale, box.mat_index));
            planes.add(new Surfaces.Plane(1, 0, 0, box.center.x + 0.5*box.scale, box.mat_index));
            planes.add(new Surfaces.Plane(0, 1, 0, box.center.y - 0.5*box.scale, box.mat_index));
            planes.add(new Surfaces.Plane(0, 1, 0, box.center.y + 0.5*box.scale, box.mat_index));
            planes.add(new Surfaces.Plane(0, 0, 1, box.center.z - 0.5*box.scale, box.mat_index));
            planes.add(new Surfaces.Plane(0, 0, 1, box.center.z + 0.5*box.scale, box.mat_index));

            for(Surfaces.Plane plane : planes) {
                if(Math.abs(p_intersection.x*plane.N.x + p_intersection.y*plane.N.y + p_intersection.z*plane.N.z - plane.offset) < 0.00001)
                    return new Vector(plane.N.x, plane.N.y, plane.N.z, true);
            }
        }
        return null;
    }

}