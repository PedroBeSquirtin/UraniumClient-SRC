package com.uranium.utils;

import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import com.uranium.UraniumClient;
import com.uranium.module.modules.client.Uranium;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

public final class Utils {
    
    public static Color getMainColor(int alpha, int index) {
        int r = Uranium.redColor.getValue();
        int g = Uranium.greenColor.getValue();
        int b = Uranium.blueColor.getValue();
        
        if (Uranium.enableRainbow.getValue()) {
            return ColorUtil.rainbow(index, alpha);
        }
        if (Uranium.enableBreathing.getValue()) {
            return ColorUtil.breathing(new Color(r, g, b, alpha), index, 20);
        }
        return new Color(r, g, b, alpha);
    }

    public static File getCurrentJarPath() throws URISyntaxException {
        return new File(UraniumClient.class.getProtectionDomain()
                       .getCodeSource().getLocation().toURI().getPath());
    }

    public static void downloadFile(String url, File output) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            
            try (InputStream is = connection.getInputStream();
                 FileOutputStream fos = new FileOutputStream(output)) {
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyVector(Vector3d target, Vec3d source) {
        target.x = source.x;
        target.y = source.y;
        target.z = source.z;
    }
    
    public static Vec3d vector3dToVec3d(Vector3d vec) {
        return new Vec3d(vec.x, vec.y, vec.z);
    }
    
    public static Vector3d vec3dToVector3d(Vec3d vec) {
        return new Vector3d(vec.x, vec.y, vec.z);
    }
    
    public static String readFile(File file) {
        try {
            return new String(java.nio.file.Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return null;
        }
    }
    
    public static void writeFile(File file, String content) {
        try {
            java.nio.file.Files.write(file.toPath(), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
