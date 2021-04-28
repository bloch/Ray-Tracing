package com.company.RayTracing;

public class FishEyes {

    /** Helper function that calculates maximum theta of rays shot using fish eyes. */
    public static double Find_Max_Theta(double f, double k) { // finding maximum fish eye ray angle, in order to later not include
        // other rays who's angle are larger. This will result in a circular image, with a black frame.
        Camera camera = RayTracer.camera; int imageHeight = RayTracer.imageHeight; int imageWidth = RayTracer.imageWidth;
        double max_deformed_radius = 0;
        double max_theta = 0;
        Point P = new Point(camera.P0.x, camera.P0.y, camera.P0.z);

        for(int i = 0; i < imageHeight; i++) {
            P.x = camera.P0.x;
            P.y = camera.P0.y;
            P.z = camera.P0.z;    //set P to origin.
            P.addVector(camera.Vy, i * (camera.screen_height / imageHeight));        //Set Height of P.

            for (int j = 0; j < imageWidth; j++) {
                // P is the current pixel point on the view plane.

                Vector V = new Vector(P, camera.position, true);    // Ray = camera.position + tV

                double small_theta = Math.acos(camera.towards.dot_product(V));
                double dist_to_xif = f / Math.cos(small_theta);
                Point xif = camera.position.addVector2(V, -1 * dist_to_xif);
                Point lens_center = camera.position.addVector2(camera.towards, -1 * f);
                double my_R = xif.dist(lens_center);
                double theta = get_theta_from_R(f, k, my_R);
                double deformed_radius = Math.abs(Math.tan(theta) * camera.screen_dist);

                if (deformed_radius > max_deformed_radius) {
                    max_deformed_radius = deformed_radius;
                    max_theta = theta;

                }
                P.addVector(camera.Vx, camera.screen_width/imageWidth);
            }
        }
        return max_theta;
    }

    /** Calculates theta using the inverse of the fish eye function. */
    public static double get_theta_from_R(double f, double k, double R) {
        if(k > 0 && k <= 1) {
            return Math.atan(R*k/f)/k;
        }
        if(k == 0) {
            return R/f;
        }
        //else: -1 <= k < 0
        return Math.asin(R*k/f)/k;
    }

}
