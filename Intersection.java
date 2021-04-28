package com.company.RayTracing;

/** This class will represent intersections of the ray with objects in the scene. */
public class Intersection {
    double t;
    Surfaces obj;

    public Intersection(double t, Surfaces obj) {
        this.t = t;
        this.obj = obj;
    }

    /** Calculates the intersection of a ray with a box using the slabs method. **/
    public static double RayBoxIntersection(Point camera_position, Vector V, Surfaces.Box box) {
        double t_x1 = RayPlaneIntersection(camera_position, V, new Surfaces.Plane(1, 0, 0, box.center.x + 0.5*box.scale, box.mat_index));
        double t_x2 = RayPlaneIntersection(camera_position, V, new Surfaces.Plane(1, 0, 0, box.center.x - 0.5*box.scale, box.mat_index));
        double t_y1 = RayPlaneIntersection(camera_position, V, new Surfaces.Plane(0, 1, 0, box.center.y + 0.5*box.scale, box.mat_index));
        double t_y2 = RayPlaneIntersection(camera_position, V, new Surfaces.Plane(0, 1, 0, box.center.y - 0.5*box.scale, box.mat_index));
        double t_z1 = RayPlaneIntersection(camera_position, V, new Surfaces.Plane(0, 0, 1, box.center.z + 0.5*box.scale, box.mat_index));
        double t_z2 = RayPlaneIntersection(camera_position, V, new Surfaces.Plane(0, 0, 1, box.center.z - 0.5*box.scale, box.mat_index));

        double tmin = Math.min(t_x1, t_x2);
        double tmax = Math.max(t_x1, t_x2);

        double t_y_min = Math.min(t_y1, t_y2);
        double t_y_max = Math.max(t_y1, t_y2);

        if((tmin > t_y_max) || (t_y_min > tmax))
            return 0;

        tmin = Math.max(tmin, t_y_min);
        tmax = Math.min(tmax, t_y_max);

        double t_z_min = Math.min(t_z1, t_z2);
        double t_z_max = Math.max(t_z1, t_z2);

        if((tmin > t_z_max) || (t_z_min > tmax))
            return 0;

        tmin = Math.max(tmin, t_z_min);

        return tmin;
    }

    /** Calculates the intersection of a ray with a plane. **/
    public static double RayPlaneIntersection(Point camera_position, Vector V, Surfaces.Plane plane) {
        double P0_N = camera_position.x*plane.N.x + camera_position.y*plane.N.y + camera_position.z*plane.N.z;
        double VN = V.y*plane.N.y +V.x*plane.N.x + V.z*plane.N.z;
        return (plane.offset - P0_N)/VN;
    }

    /** Calculates the intersection of a ray with a sphere, using the method from the lecture notes. **/
    public static double RaySphereIntersection(Point camera_position, Vector V, Surfaces.Sphere sphere) {
        Vector L = new Vector(sphere.center.x - camera_position.x, sphere.center.y - camera_position.y, sphere.center.z - camera_position.z, false);
        double t_ca = L.dot_product(V);
        if (t_ca < 0)
            return 0;
        double d_squared = (L.x*L.x + L.y*L.y+ L.z*L.z) - t_ca*t_ca;
        if(d_squared > sphere.radius*sphere.radius)
            return 0;
        double t_hc = Math.sqrt(sphere.radius*sphere.radius - d_squared);
        return t_ca - t_hc;
    }



}