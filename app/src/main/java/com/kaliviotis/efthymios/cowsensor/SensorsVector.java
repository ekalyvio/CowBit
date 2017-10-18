package com.kaliviotis.efthymios.cowsensor;

/**
 * Created by Efthymios on 10/16/2017.
 */

/** struct sensors_vec_s is used to return a vector in a common format. */
public class SensorsVector  {
    private float[] vector = { 0.0F, 0.0F, 0.0F };

    public SensorsVector(float x, float y, float z) {
        setVector(x, y, z);
    }

    public void setVector(float x, float y, float z) {
        vector[0] = x;
        vector[1] = y;
        vector[2] = z;
    }

    public float[] getVector() {
        return vector;
    }

    public void setX(float x) { vector[0] = x; }
    public void setY(float y) { vector[1] = y; }
    public void setZ(float z) { vector[2] = z; }

    public float getX() { return vector[0]; }
    public float getY() { return vector[1]; }
    public float getZ() { return vector[2]; }

    public void setRoll(float roll) { vector[0] = roll; }
    public void setPitch(float pitch) { vector[1] = pitch; }
    public void setHeading(float heading) { vector[2] = heading; }

    public float getRoll() { return vector[0]; }
    public float getPitch() { return vector[1]; }
    public float getHeading() { return vector[2]; }

/*    public int status;
    Byte[] reserved;*/
}
