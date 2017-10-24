package com.kaliviotis.efthymios.cowsensor.sensorapp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Efthymios on 10/16/2017.
 */

public class RawDatastorage implements Runnable {
    private ArrayList<SensorFusion.IMUEvent> eventsList;

    public void SetList(ArrayList<SensorFusion.IMUEvent> lst) {
        eventsList = lst;
    }

    private byte[] toByteBuffer(float[] gyro, float[] accel, float[] magn) {
        ByteBuffer buffer = ByteBuffer.allocate(3 * 4 * gyro.length);

        for (float value : gyro)
            buffer.putFloat(value);
        for (float value : accel)
            buffer.putFloat(value);
        for (float value : magn)
            buffer.putFloat(value);

        return buffer.array();
    }

    @Override
    public void run() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH");
        Date date = new Date();

        String fileName = "Raw/Raw_" + dateFormat.format(date) + ".dat";

        OutputStream output = null;
        try {
            File fl = new File(Globals.getInstance().getContext().getFilesDir(), fileName);
            output = new BufferedOutputStream(new FileOutputStream(fl, true));
            for(SensorFusion.IMUEvent evt : eventsList) {
                float[] gyro = evt.gyro.getGyro().getVector();
                float[] accel = evt.gyro.getAcceleration().getVector();
                float[] magn = evt.gyro.getMagnetic().getVector();

                byte[] buff = toByteBuffer(gyro, accel, magn);

                output.write(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
