package com.company.RayTracing;

/** This class represent the camera & screen setting required for the ray casting implementation. */
public class Camera {
    Point position;             //camera position (x,y,z).

    double screen_dist;         //focal distance from camera position to screen.
    double screen_width;        //screen width.
    double screen_height;       //screen height.
    Point P0;                   //P0 is the corner of the screen.
    Point screen_center;        //point representing the screen center.

    boolean fish_eye;           //true iff fish eye lens is activated
    double K_fish_eyes;         //parameter of fish eye lens when activated

    Vector towards;             //direction of the screen from the camera.
    Vector up;                  //describing the up vector of the world.
    Vector Vx;                  //describing x axis of the screen.
    Vector Vy;                  //describing y axis of the screen.

    public Camera(double pos_x, double pos_y, double pos_z, double look_x, double look_y, double look_z, double up_x, double up_y, double up_z, double screen_dist, double screen_width, boolean fish_eyes, double K_fish_eyes) {
        this.position = new Point(pos_x, pos_y, pos_z);
        this.screen_dist = screen_dist;
        this.screen_width = screen_width;
        this.fish_eye = fish_eyes;
        this.K_fish_eyes = K_fish_eyes;

        //towards vector points to the lookat point from the camera position.
        this.towards = new Vector(new Point(look_x, look_y, look_z), position, true);
        //right vector is calculated by the cross product of towards and up.
        Vector right = Vector.cross_product(towards, new Vector(up_x, up_y, up_z, true));
        //up vector is then fixed to be the cross product of right and towards.
        this.up = Vector.cross_product(towards, right);

        //Matrix M calculations to find out Vx, Vy vectors of the screen.
        double sinx = -towards.y;
        double cosx = Math.sqrt(1 - sinx*sinx);
        double siny = -1*towards.x / cosx;
        double cosy = towards.z / cosx;

        double[][] M = {{cosy, 0, siny},
                        {-1*sinx*siny, cosx, sinx*cosy},
                        {-1*cosx*siny, -1*sinx, cosx*cosy}};

        this.Vx = Vector.leftMult(new Vector(1, 0, 0, false), M);
        this.Vy = Vector.leftMult(new Vector(0,-1,0, false), M);
    }
}