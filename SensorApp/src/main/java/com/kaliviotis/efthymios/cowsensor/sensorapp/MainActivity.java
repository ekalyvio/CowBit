package com.kaliviotis.efthymios.cowsensor.sensorapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {
//    private static final int INTERVAL_BETWEEN_VALUES = 10;
//    private Handler mHandler = new Handler();

    SensorFusion sensorFusion;
    Thread sensorFusionThread;

    // echo -n 1 > /sys/module/i2c_bcm2708/parameters/combined
    // ??????????????????????????????????????????
    // setprop debug.assert 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

        SensorFusion sensorFusion = new SensorFusion();
        try {
            sensorFusion.Init();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        sensorFusionThread = new Thread(sensorFusion);
        sensorFusionThread.setPriority(Thread.MAX_PRIORITY);
        sensorFusionThread.start();

        Globals.getInstance().setContext(getApplicationContext());

        Log.d("MAIN", "Data files dir: " + Globals.getInstance().getContext().getFilesDir());
        File directory = new File(getFilesDir() + File.separator + "Steps");
        directory.mkdirs();
        directory = new File(getFilesDir() + File.separator + "Raw");
        directory.mkdirs();
//            mHandler.post(mGyroRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();

        sensorFusionThread.interrupt();
        sensorFusion.Close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Step 4. Remove handler events on close.
//        mHandler.removeCallbacks(mGyroRunnable);
    }


    private Runnable mGyroRunnable = new Runnable() {
        @Override
        public void run() {
            // Exit if the GPIO is already closed
/*            if ((dev1 == null) | (dev2 == null)) {
                return;
            }*/

/*                Adafruit_Sensor.SensorEvent aaa;
                if (triggered) {
                    triggered = false;
                    Log.d("mBCM15AccelCallback", "interrupt triggered " + System.currentTimeMillis());

                    int flag = accell.getIntSourceFlag();
                    int flagMagn = accell.getMagnIntSourceFlag();
                    aaa = accell.getEvent();
                }*/
                //Adafruit_Sensor.SensorEvent aaa = accell.getEvent();

/*
                int FEvent = gyro.getFEvent();
                int FStatus = gyro.getFStatus();

                String txt = String.format("IsFifoOverflow: %b IsWatermarkEvent: %b getFifoSamplesStored: %d", gyro.IsFifoOverflow(FStatus), gyro.IsWatermarkEvent(FStatus), gyro.getFifoSamplesStored(FStatus));
                Log.d("MainActivity", "Fifo -> " + txt);
                txt = String.format("IsFEventDetected: %b getODRPeriodsElapsedSinceFEvent: %d", gyro.IsFEventDetected(FEvent), gyro.getODRPeriodsElapsedSinceFEvent(FEvent));
                Log.d("MainActivity", "Event -> " + txt);

                int cnt = 0;
                while (gyro.getFifoSamplesStored(FStatus) > 15) {
                    cnt++;
                    //gyro.BulkReadEvents(5);
//                    Adafruit_Sensor.SensorEvent evt = gyro.getEvent();

  //                  int status = evt.getGyro().status;
//                    String text = String.format("%d - (X,Y,F): %f,%f,%f", evt.timestamp, evt.getGyro().getX(), evt.getGyro().getY(), evt.getGyro().getZ());
//                    Log.d("MainActivity", "gyroEvent: " + text);

                    FEvent = gyro.getFEvent();
                    FStatus = gyro.getFStatus();

                    txt = String.format("IsFifoOverflow: %b IsWatermarkEvent: %b getFifoSamplesStored: %d", gyro.IsFifoOverflow(FStatus), gyro.IsWatermarkEvent(FStatus), gyro.getFifoSamplesStored(FStatus));
                    Log.d("MainActivity", "Fifo -> " + txt);
                    txt = String.format("IsFEventDetected: %b getODRPeriodsElapsedSinceFEvent: %d", gyro.IsFEventDetected(FEvent), gyro.getODRPeriodsElapsedSinceFEvent(FEvent));
                    Log.d("MainActivity", "Event -> " + txt);
                }

                Log.d("MainActivity", "---------------- Events read: " + String.valueOf(cnt));
*/
                // Step 4. Schedule another event after delay.
//                mHandler.postDelayed(mGyroRunnable, INTERVAL_BETWEEN_VALUES);
        }
    };
}
