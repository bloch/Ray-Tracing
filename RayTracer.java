package com.company.RayTracing;

import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

/** Main class of the ray tracing algorithm. */
public class RayTracer {
    public static int imageWidth;
    public static int imageHeight;

    public static Random randomno = new Random();

    public static Camera camera;
    public static Scene scene;

    /** Runs the ray tracer. Takes scene file, output image file and image size as input. */
    public static void main(String[] args) {
        try {
            RayTracer tracer = new RayTracer();

            tracer.imageWidth = 500; tracer.imageHeight = 500;

            if (args.length < 2)
                throw new RayTracerException("Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");

            String sceneFileName = args[0];
            String outputFileName = args[1];

            if (args.length > 3)
            {
                tracer.imageWidth = Integer.parseInt(args[2]);
                tracer.imageHeight = Integer.parseInt(args[3]);
            }
            tracer.parseScene(sceneFileName);

            camera.screen_height = ((double) tracer.imageHeight / (double) tracer.imageWidth) * camera.screen_width;

            camera.screen_center = new Point(camera.position.x + camera.screen_dist*camera.towards.x,
                                             camera.position.y + camera.screen_dist*camera.towards.y,
                                            camera.position.z + camera.screen_dist*camera.towards.z);

            camera.P0 = new Point(camera.screen_center.x - 0.5*camera.screen_width*camera.Vx.x - 0.5*camera.screen_height*camera.Vy.x,
                                  camera.screen_center.y - 0.5*camera.screen_width*camera.Vx.y - 0.5*camera.screen_height*camera.Vy.y,
                                  camera.screen_center.z - 0.5*camera.screen_width*camera.Vx.z - 0.5*camera.screen_height*camera.Vy.z);

            tracer.renderScene(outputFileName);

//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//        } catch (RayTracerException e) {
//            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** Parses the scene file and creates the scene and all required objects. */
    public void parseScene(String sceneFileName) throws IOException
    {
        FileReader fr = new FileReader(sceneFileName);
        BufferedReader r = new BufferedReader(fr);
        String line;
        int lineNum = 0;
        System.out.println("Started parsing scene file " + sceneFileName);

        while ((line = r.readLine()) != null)
        {
            line = line.trim();
            ++lineNum;

            if (line.isEmpty() || (line.charAt(0) == '#'))
            {  // This line in the scene file is a comment
                //continue;
            }
            else
            {
                String code = line.substring(0, 3).toLowerCase();
                // Split according to white space characters:
                String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

                if (code.equals("cam"))
                {
                    boolean fish_eyes = false;
                    double k_fish_eyes = 0.5;
                    if(params.length == 12) {
                        fish_eyes = Boolean.parseBoolean(params[11]);
                    }
                    if(params.length == 13) {
                        fish_eyes = Boolean.parseBoolean(params[11]);
                        k_fish_eyes = Double.parseDouble(params[12]);
                    }
                    camera = new Camera(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
                                             Double.parseDouble(params[2]), Double.parseDouble(params[3]),
                                             Double.parseDouble(params[4]), Double.parseDouble(params[5]),
                                             Double.parseDouble(params[6]), Double.parseDouble(params[7]),
                                             Double.parseDouble(params[8]), Double.parseDouble(params[9]),
                                             Double.parseDouble(params[10]), fish_eyes, k_fish_eyes);
                }
                else if (code.equals("set"))
                {
                    this.scene = new Scene(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
                                           Double.parseDouble(params[2]), Integer.parseInt(params[3]),
                                           Integer.parseInt(params[4]));
                }
                else if (code.equals("mtl"))
                {
                    Material new_mat = new Material(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
                            Double.parseDouble(params[2]), Double.parseDouble(params[3]),
                            Double.parseDouble(params[4]), Double.parseDouble(params[5]),
                            Double.parseDouble(params[6]), Double.parseDouble(params[7]),
                            Double.parseDouble(params[8]), Double.parseDouble(params[9]),
                            Double.parseDouble(params[10]));

                    scene.materials.add(new_mat);
                }
                else if (code.equals("sph"))
                {
                    Surfaces.Sphere new_sphere = new Surfaces.Sphere(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
                            Double.parseDouble(params[2]), Double.parseDouble(params[3]),
                            Integer.parseInt(params[4]) - 1);

                    scene.spheres.add(new_sphere);
                }
                else if (code.equals("pln"))
                {
                    Surfaces.Plane new_plane = new Surfaces.Plane(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
                            Double.parseDouble(params[2]), Double.parseDouble(params[3]),
                            Integer.parseInt(params[4]) - 1);

                    scene.planes.add(new_plane);
                }
                else if (code.equals("lgt"))
                {
                    Light new_light = new Light(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
                            Double.parseDouble(params[2]), Double.parseDouble(params[3]),
                            Double.parseDouble(params[4]), Double.parseDouble(params[5]),
                            Double.parseDouble(params[6]), Double.parseDouble(params[7]),
                            Double.parseDouble(params[8]));
                    scene.lights.add(new_light);
                }
                else if (code.equals("box")) {
                    Surfaces.Box new_box = new Surfaces.Box(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
                            Double.parseDouble(params[2]), Double.parseDouble(params[3]), Integer.parseInt(params[4]) - 1);

                    scene.boxes.add(new_box);
                }
                else {
                    System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
                }
            }
        }
        System.out.println("Finished parsing scene file " + sceneFileName);
    }

    /** Renders the loaded scene and saves it to the specified file location. */
    public void renderScene(String outputFileName) throws RayTracerException {
        long startTime = System.currentTimeMillis();

        byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];

        RayCaster(rgbData);

        long endTime = System.currentTimeMillis();
        long renderTime = endTime - startTime;

        System.out.println("Finished rendering scene in " + renderTime + " milliseconds.");
        saveImage(this.imageWidth, rgbData, outputFileName);
        System.out.println("Saved file " + outputFileName);
    }

    /** Main ray casting function. This functions controls the rays that it casts and updates the RGB data. */
    public void RayCaster(byte[] rgbData) throws RayTracerException {
        //variables relevant only for fish eyes calculations:
        double f = camera.screen_dist;
        double k = camera.K_fish_eyes;
        double theta = 0;
        double max_theta = 0;
        if (camera.fish_eye)
            max_theta = FishEyes.Find_Max_Theta(f, k);
        //end of fish-eye variable declaration

        Point P = new Point(camera.P0.x, camera.P0.y, camera.P0.z); // constructing origin point on screen

        for(int i = 0; i < imageHeight; i++) {
            P.x = camera.P0.x; P.y = camera.P0.y; P.z = camera.P0.z;    //set P to origin.
            P.addVector(camera.Vy, i*(camera.screen_height/imageHeight));        //Set Height of P.

            for(int j = 0; j < imageWidth; j++) {
                // P is the current pixel point on the view plane.

                Vector V = new Vector(P, camera.position, true);    // Ray = camera.position + tV
                if (camera.fish_eye) { // building deformed fish eye ray
                    double small_theta = Math.acos(camera.towards.dot_product(V));
                    double dist_to_xif = f / Math.cos(small_theta);
                    Point xif = camera.position.addVector2(V, -1*dist_to_xif);
                    Point lens_center = camera.position.addVector2(camera.towards, -1*f);
                    double my_R = xif.dist(lens_center);
                    theta = FishEyes.get_theta_from_R(f, k, my_R);
                    double deformed_radius = Math.abs(Math.tan(theta)* camera.screen_dist);
                    Vector from_center = new Vector(P, camera.screen_center, true);
                    Point new_p = camera.screen_center.addVector2(from_center, deformed_radius);
                    V = new Vector(new_p, camera.position, true); // Ray = camera.position + tV
                }
                else {  // building regular ray
                    V = new Vector(P, camera.position, true); // Ray = camera.position + tV
                }

                // Now find intersection
                Intersection intersection = FindIntersection(camera.position, V, scene);

                double[] next_rgb = GetColor(intersection, camera.position, V, 1);

                if (theta <= max_theta || !camera.fish_eye) {
                    rgbData[(i*imageWidth + j)*3] = (byte)(255 * next_rgb[0]);
                    rgbData[(i*imageWidth + j)*3 + 1] = (byte)(255 * next_rgb[1]);
                    rgbData[(i*imageWidth + j)*3 + 2] = (byte)(255 * next_rgb[2]);
                }
                else {
                    rgbData[(i*imageWidth + j)*3] = (byte)(0);
                    rgbData[(i*imageWidth + j)*3 + 1] = (byte)(0);
                    rgbData[(i*imageWidth + j)*3 + 2] = (byte)(0);
                }

                P.addVector(camera.Vx, camera.screen_width/imageWidth);
            }
        }
    }

    /** Main color computing function. */
    public double[] GetColor(Intersection intersection,Point camera_position, Vector V, int cur_depth) throws RayTracerException {
        if(intersection.obj == null)
            return new double[]{scene.background_color.R, scene.background_color.G, scene.background_color.B};

        RGB color = new RGB(0, 0, 0);
        RGB diff = new RGB(0, 0, 0);
        RGB specular = new RGB(0, 0, 0);

        /** Ambient light code */
        RGB avg = scene.calcAVGcolor();
        color.R += scene.materials.get(intersection.obj.mat_index).ambient_coef * avg.R;
        color.G += scene.materials.get(intersection.obj.mat_index).ambient_coef * avg.G;
        color.B += scene.materials.get(intersection.obj.mat_index).ambient_coef * avg.B;
        /** Ambient light code */

        Point p_intersection = camera_position.addVector2(V, intersection.t);   //(x,y,z) of the intersection point.

        Vector N = Surfaces.calcNormalToSurface(intersection, camera_position, V);    //Normal Vector to the surface intersected.

        for(Light light: scene.lights) {
            // Calculations of the light intensity of the soft shadows.
            double light_intensity = SoftShadows.soft_shadows(light, p_intersection,intersection);

            // Calculations of different parameters needed for the diffuse & specular RGB colors.
            Vector L = new Vector(light.position, p_intersection,true);
            double NL = Math.max(0, L.dot_product(N));
            Vector R = new Vector(2*NL*N.x - L.x, 2*NL*N.y - L.y, 2*NL*N.z - L.z, true);
            Vector V_1 = new Vector(-1 * V.x, -1 * V.y, -1 * V.z, true);
            double RV = Math.max(0, R.dot_product(V_1));

            // Updating diffuse colors of the point, including soft shadows parameter.
            diff.R += light_intensity*light.color.R * scene.materials.get(intersection.obj.mat_index).diffuse.R * NL;
            diff.G += light_intensity*light.color.G * scene.materials.get(intersection.obj.mat_index).diffuse.G * NL;
            diff.B += light_intensity*light.color.B * scene.materials.get(intersection.obj.mat_index).diffuse.B * NL;

            // Updating specular colors of the point, including soft shadows parameter and specular intensity parameter.
            specular.R += light_intensity*light.specular_intensity * light.color.R * scene.materials.get(intersection.obj.mat_index).specular.R * Math.pow(RV, scene.materials.get(intersection.obj.mat_index).phong_coef);
            specular.G += light_intensity*light.specular_intensity * light.color.G * scene.materials.get(intersection.obj.mat_index).specular.G * Math.pow(RV, scene.materials.get(intersection.obj.mat_index).phong_coef);
            specular.B += light_intensity*light.specular_intensity * light.color.B * scene.materials.get(intersection.obj.mat_index).specular.B * Math.pow(RV, scene.materials.get(intersection.obj.mat_index).phong_coef);
        }

        //Sum of the diffuse & specular colors with the parameter of (1-transparency) as required in the formula in pdf.
        color.R = (diff.R + specular.R)*(1-scene.materials.get(intersection.obj.mat_index).transparency);
        color.G = (diff.G + specular.G)*(1-scene.materials.get(intersection.obj.mat_index).transparency);
        color.B = (diff.B + specular.B)*(1-scene.materials.get(intersection.obj.mat_index).transparency);

        // Calculations of the reflection color at the point.
        if(cur_depth < scene.max_recursion_depth) {
            //Calculations of the reflected ray to be casted from the intersection point.
            double VN = V.dot_product(N);
            Vector R = new Vector(V.x - 2*VN*N.x, V.y - 2*VN*N.y, V.z - 2*VN*N.z, true);

            //Calculations of the intersection of reflected ray with other objects(excluding current object intersected).
            Intersection reflection_intersection = FindIntersectionWithOutSurface(p_intersection, R, scene, intersection);

            //Calculations of the reflection color of the object hit by the reflected ray.
            double[] reflect_color = GetColor(reflection_intersection, p_intersection, R, cur_depth + 1);
            RGB reflection_color = new RGB(reflect_color[0], reflect_color[1], reflect_color[2]);

            //Summing the reflection colors RGB to the total color of the point.
            color.R += reflection_color.R * scene.materials.get(intersection.obj.mat_index).reflection.R;
            color.G += reflection_color.G * scene.materials.get(intersection.obj.mat_index).reflection.G;
            color.B += reflection_color.B * scene.materials.get(intersection.obj.mat_index).reflection.B;
        }

        // Calculations of the transparency color at the point.
        if(cur_depth < scene.max_recursion_depth) {
            if(scene.materials.get(intersection.obj.mat_index).transparency != 0) {
                //Calculations of the intersection of transparency ray with other objects(excluding current object intersected).
                Intersection transp_intersection = FindIntersectionWithOutSurface(p_intersection, V, scene, intersection);

                //Calculations of the transparency color of the object hit by the transparency ray.
                double[] transp_color = GetColor(transp_intersection, p_intersection, V, cur_depth);
                RGB transparency_color = new RGB(transp_color[0], transp_color[1], transp_color[2]);

                //Summing the transparency colors RGB to the total color of the point, including transparency parameter.
                color.R += transparency_color.R * scene.materials.get(intersection.obj.mat_index).transparency;
                color.G += transparency_color.G * scene.materials.get(intersection.obj.mat_index).transparency;
                color.B += transparency_color.B * scene.materials.get(intersection.obj.mat_index).transparency;
            }
        }

        return new double[]{Math.min(color.R, 1), Math.min(color.G, 1), Math.min(color.B, 1)};
    }

    /** Calculates the first intersection of the ray with the objects in the scene, not including a particular object given. NULL is returned if no intersection. **/
    public Intersection FindIntersectionWithOutSurface(Point camera_position, Vector V, Scene scene, Intersection intersection) {
        double first_t = Integer.MAX_VALUE;
        Surfaces best_obj = null;
        for(Surfaces.Sphere sphere : scene.spheres) {
            if(sphere != intersection.obj) {
                double intersect_t = Intersection.RaySphereIntersection(camera_position, V, sphere);
                if (intersect_t <= 0)
                    continue;
                if (intersect_t < first_t) {
                    best_obj = sphere;
                    first_t = intersect_t;
                }
            }
        }
        for(Surfaces.Plane plane : scene.planes) {
            if(plane != intersection.obj) {
                double intersect_t = Intersection.RayPlaneIntersection(camera_position, V, plane);
                if (intersect_t <= 0)
                    continue;
                if (intersect_t < first_t) {
                    best_obj = plane;
                    first_t = intersect_t;
                }
            }
        }
        for(Surfaces.Box box : scene.boxes) {
            if(box != intersection.obj) {
                double intersect_t = Intersection.RayBoxIntersection(camera_position, V, box);
                if (intersect_t <= 0)
                    continue;
                if (intersect_t < first_t) {
                    best_obj = box;
                    first_t = intersect_t;
                }
            }
        }
        return new Intersection(first_t, best_obj);
    }

    /** Calculates the first intersection of the ray with the objects in the scene. NULL is returned if no intersection. **/
    public Intersection FindIntersection(Point camera_position, Vector V, Scene scene) {
        double first_t = Integer.MAX_VALUE;
        Surfaces best_obj = null;
        for(Surfaces.Sphere sphere : scene.spheres) {
            double intersect_t = Intersection.RaySphereIntersection(camera_position, V, sphere);
            if(intersect_t <= 0)
                continue;
            if(intersect_t < first_t) {
                best_obj = sphere;
                first_t = intersect_t;
            }
        }
        for(Surfaces.Plane plane : scene.planes) {
            double intersect_t = Intersection.RayPlaneIntersection(camera_position, V, plane);
            if(intersect_t <= 0)
                continue;
            if(intersect_t < first_t) {
                best_obj = plane;
                first_t = intersect_t;
            }
        }
        for(Surfaces.Box box : scene.boxes) {
            double intersect_t = Intersection.RayBoxIntersection(camera_position, V, box);
            if(intersect_t <= 0)
                continue;
            if(intersect_t < first_t) {
                best_obj = box;
                first_t = intersect_t;
            }
        }
        return new Intersection(first_t, best_obj);
    }
    //////////////////////// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT //////////////////////////////////////////

    /** Saves RGB data as an image in png format to the specified location. **/
    public static void saveImage(int width, byte[] rgbData, String fileName)
    {
        try {
            BufferedImage image = bytes2RGB(width, rgbData);
            ImageIO.write(image, "png", new File(fileName));
        } catch (IOException e) {
            System.out.println("ERROR SAVING FILE: " + e.getMessage());
        }
    }

    /** Producing a BufferedImage that can be saved as png from a byte array of RGB values. **/
    public static BufferedImage bytes2RGB(int width, byte[] buffer) {
        int height = buffer.length / width / 3;
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, false,
                Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        DataBufferByte db = new DataBufferByte(buffer, width * height);
        WritableRaster raster = Raster.createWritableRaster(sm, db, null);
        return new BufferedImage(cm, raster, false, null);
    }

    /** Ray tracer exception class. **/
    public static class RayTracerException extends Exception {
        public RayTracerException(String msg) {  super(msg); }
    }
}