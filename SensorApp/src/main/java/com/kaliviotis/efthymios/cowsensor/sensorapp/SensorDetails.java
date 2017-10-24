package com.kaliviotis.efthymios.cowsensor.sensorapp;

/**
 * Created by Efthymios on 10/16/2017.
 */

/** struct sensor_s is used to describe basic information about a specific sensor. */
public class SensorDetails
{
    String name;                        /**< sensor name */
    int  version;                         /**< version of the hardware + driver */
    int sensor_id;                       /**< unique sensor identifier */
    Adafruit_Sensor.SensorsType type;                            /**< this sensor's type (ex. SENSOR_TYPE_LIGHT) */
    float max_value;                       /**< maximum value of this sensor's value in SI units */
    float min_value;                       /**< minimum value of this sensor's value in SI units */
    float resolution;                      /**< smallest difference between two values reported by this sensor */
    int min_delay;                       /**< min delay in microseconds between events. zero = not a constant rate */
}
