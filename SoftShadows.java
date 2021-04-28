package com.company.RayTracing;

import java.util.Random;

public class SoftShadows {

    /** This function implements the soft shadows algorithm, and returns the light intensity parameter calculated. **/
    public static double soft_shadows(Light light, Point p_intersection, Intersection intersection) throws RayTracer.RayTracerException {
        Random randomno = RayTracer.randomno;
        Scene scene = RayTracer.scene;

        Vector to_surface = new Vector(p_intersection, light.position, true);
        double normal_offset = light.position.x*to_surface.x + light.position.y*to_surface.y + light.position.z*to_surface.z;
        Surfaces.Plane light_plane = new Surfaces.Plane(to_surface.x, to_surface.y, to_surface.z, normal_offset, 0);
        Point point_on_plane;
        if(light_plane.N.z != 0) {
            double rand_x = randomno.nextDouble() * 10;
            double rand_y = randomno.nextDouble() * 20;
            point_on_plane = new Point(rand_x, rand_y, (light_plane.offset - light_plane.N.x * rand_x - light_plane.N.y * rand_y) / light_plane.N.z);
        }
        else if(light_plane.N.y != 0) { //here N.z = 0, and N.y != 0.
            double rand_x = randomno.nextDouble() * 10;
            double rand_z = randomno.nextDouble() * 20;
            point_on_plane = new Point(rand_x, (light_plane.offset - light_plane.N.x * rand_x)/light_plane.N.y,rand_z);
        }
        else if(light_plane.N.x != 0) { //here N.z = 0, N.y = 0, N.x != 0.
            double rand_y = randomno.nextDouble() * 10;
            double rand_z = randomno.nextDouble() * 20;
            point_on_plane = new Point(light_plane.offset/light_plane.N.x, rand_y, rand_z);
        }
        else { //N.z = N.y = N.z = 0
            throw new RayTracer.RayTracerException("Exception in the soft shadows algorithm: couldn't calculate a plane for the light source.");
        }

        Vector light_Vx = new Vector(point_on_plane, light.position, true);
        Vector light_Vy = Vector.cross_product(light_Vx, to_surface);
        Point rectangle_start = new Point(light.position.x - 0.5*light.light_radius*light_Vx.x - 0.5*light.light_radius*light_Vy.x,
                light.position.y - 0.5*light.light_radius*light_Vx.y - 0.5*light.light_radius*light_Vy.y,
                light.position.z - 0.5*light.light_radius*light_Vx.z - 0.5*light.light_radius*light_Vy.z);

        double counter = 0;
        Point running_point = new Point(0, 0, 0);
        for(int i = 0; i < scene.num_of_shadow_rays; i++) {
            running_point.x = rectangle_start.x; running_point.y = rectangle_start.y; running_point.z = rectangle_start.z;
            running_point = running_point.addVector2(light_Vy, i*(light.light_radius/scene.num_of_shadow_rays));
            for(int j = 0; j < scene.num_of_shadow_rays; j++) {
                Point running_point_tmp = new Point(running_point.x, running_point.y, running_point.z);
                running_point = running_point.addVector2(light_Vx, randomno.nextDouble()*(light.light_radius/scene.num_of_shadow_rays));
                running_point = running_point.addVector2(light_Vy, randomno.nextDouble()*(light.light_radius/scene.num_of_shadow_rays));

                Vector soft_shadow_ray = new Vector(p_intersection, running_point, true);
                double t = new Vector(p_intersection, running_point, false).norm();

                counter += shadow_param(running_point, soft_shadow_ray, scene, intersection, t);

                running_point = running_point_tmp.addVector2(light_Vx, light.light_radius/scene.num_of_shadow_rays);
            }
        }
        double hit_percent = counter / ((double)scene.num_of_shadow_rays*(double)scene.num_of_shadow_rays);
        return (1-light.shadow_intensity) + light.shadow_intensity*hit_percent;
    }

    /** Calculates the parameter of the light intensity reached to the surface, with respect to transparent objects on the way. **/
    public static double shadow_param(Point camera_position, Vector V, Scene scene, Intersection intersection, double t) {
        double shadow = 1;
        for(Surfaces.Sphere sphere : scene.spheres) {
            if(sphere != intersection.obj) {
                double intersect_t = Intersection.RaySphereIntersection(camera_position, V, sphere);
                if (intersect_t <= 0)
                    continue;
                if (intersect_t < t)
                    shadow *= scene.materials.get(sphere.mat_index).transparency;
            }
        }
        for(Surfaces.Plane plane : scene.planes) {
            if(plane != intersection.obj) {
                double intersect_t = Intersection.RayPlaneIntersection(camera_position, V, plane);
                if (intersect_t <= 0)
                    continue;
                if (intersect_t < t)
                    shadow *= scene.materials.get(plane.mat_index).transparency;
            }
        }
        for(Surfaces.Box box : scene.boxes) {
            if(box != intersection.obj) {
                double intersect_t = Intersection.RayBoxIntersection(camera_position, V, box);
                if (intersect_t <= 0)
                    continue;
                if (intersect_t < t)
                    shadow *= scene.materials.get(box.mat_index).transparency;
            }
        }
        return shadow;
    }


}
