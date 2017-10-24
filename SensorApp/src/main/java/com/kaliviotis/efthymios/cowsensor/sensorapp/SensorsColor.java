package com.kaliviotis.efthymios.cowsensor.sensorapp;

/**
 * Created by Efthymios on 10/16/2017.
 */

/** struct sensors_color_s is used to return color data in a common format. */
public class SensorsColor {
    private float[] color = { 0.0F, 0.0F, 0.0F, 0.0F };

    public SensorsColor(float r, float g, float b, float a) {
        setColor(r, g, b, a);
    }

    public void setColor(float r, float g, float b, float a) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

    public float[] getColor() {
        return color;
    }

    // values 0..255
    public void setRGBA(int r, int g, int b, int a) {
        setColor((float)r / 255, (float)g / 255, (float)b / 255, (float)a / 255);
    }

    public int[] getRGBA() {
        return new int[] {
                (int)(color[0] * 255),
                (int)(color[1] * 255),
                (int)(color[2] * 255),
                (int)(color[3] * 255)
        };
    }
}
