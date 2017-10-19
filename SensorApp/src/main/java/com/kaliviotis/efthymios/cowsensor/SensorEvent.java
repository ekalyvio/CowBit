package com.kaliviotis.efthymios.cowsensor;

/**
 * Created by Efthymios on 10/16/2017.
 */

public class SensorEvent {
    public int version;                          /**< must be sizeof(struct sensors_event_t) */
    public int sensor_id;                        /**< unique sensor identifier */
    public int type;                             /**< sensor type */
    public int reserved0;                        /**< reserved */
    public long timestamp;                        /**< time is in milliseconds */

    public float data[];

    private SensorsVector sensVector = null;
    private SensorsVector GetSensorVector() {
        if (sensVector == null)
            sensVector = new SensorsVector(0, 0, 0);
        return sensVector;
    }

    public SensorsVector getAcceleration() { return GetSensorVector(); }
    public SensorsVector getMagnetic() { return GetSensorVector(); }
    public SensorsVector getOrientation() { return GetSensorVector(); }
    public SensorsVector getGyro() { return GetSensorVector(); }

    private float value;
    public float getTemperature() { return value; }
    public float getDistance() { return value; }
    public float getLight() { return value; }
    public float getPressure() { return value; }
    public float getRelative_humidity() { return value; }
    public float getCurrent() { return value; }
    public float getVoltage() { return value; }

    public void setTemperature(float val) { value = val; }
    public void setDistance(float val) { value = val; }
    public void setLight(float val) { value = val; }
    public void setPressure(float val) { value = val; }
    public void setRelative_humidity(float val) { value = val; }
    public void setCurrent(float val) { value = val; }
    public void setVoltage(float val) { value = val; }

    public SensorsColor color;                /**< color in RGB component values */
}
