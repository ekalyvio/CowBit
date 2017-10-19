package com.kaliviotis.efthymios.cowsensor;

/**
 * Created by Efthymios on 10/3/2017.
 */

public abstract class Adafruit_Sensor {
    public static float SENSORS_GRAVITY_EARTH = 9.80665F;              // Earth's gravity in m/s^2
    public static float SENSORS_GRAVITY_MOON = 1.6F;                   // The moon's gravity in m/s^2
    public static float SENSORS_GRAVITY_SUN = 275.0F;                  // The sun's gravity in m/s^2
    public static float SENSORS_GRAVITY_STANDARD = 9.80665F;
    public static float SENSORS_MAGFIELD_EARTH_MAX = 60.0F;            // Maximum magnetic field on Earth's surface
    public static float SENSORS_MAGFIELD_EARTH_MIN = 30.0F;            // Minimum magnetic field on Earth's surface
    public static float SENSORS_PRESSURE_SEALEVELHPA = 1013.25F;       // Average sea level pressure is 1013.25 hPa
    public static float SENSORS_DPS_TO_RADS = 0.017453293F;            // Degrees/s to rad/s multiplier
    public static float SENSORS_GAUSS_TO_MICROTESLA = 100.0F;          // Gauss to micro-Tesla multiplier

    /* Sensor types */
    public enum SensorsType {
        SENSOR_TYPE_ACCELEROMETER(1),   /**< Gravity + linear acceleration */
        SENSOR_TYPE_MAGNETIC_FIELD(2),
        SENSOR_TYPE_ORIENTATION(3),
        SENSOR_TYPE_GYROSCOPE(4),
        SENSOR_TYPE_LIGHT(5),
        SENSOR_TYPE_PRESSURE(6),
        SENSOR_TYPE_PROXIMITY(8),
        SENSOR_TYPE_GRAVITY(9),
        SENSOR_TYPE_LINEAR_ACCELERATION(10),  /**< Acceleration not including gravity */
        SENSOR_TYPE_ROTATION_VECTOR(11),
        SENSOR_TYPE_RELATIVE_HUMIDITY(12),
        SENSOR_TYPE_AMBIENT_TEMPERATURE(13),
        SENSOR_TYPE_VOLTAGE(15),
        SENSOR_TYPE_CURRENT(16),
        SENSOR_TYPE_COLOR(17);

        public final int sensorType;

        SensorsType(int type) {
            this.sensorType = type;
        }
    }
}
