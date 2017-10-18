package com.kaliviotis.efthymios.cowsensor;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Efthymios on 10/14/2017.
 */

public class SensorFusion implements Runnable {
    // Raspberry Pi 3
    // --------------
    public static String I2CInterface = "I2C1";
    public static String GyroInterruptPin = "BCM4";
    public static String AccelInterruptPin = "BCM14";

    // Pico Pi
    // -------
    // public static String I2CInterface = "I2C2";
    // public static String GyroInterruptPin = "GPIO1_IO18";
    // public static String AccelInterruptPin = "GPIO1_IO19";


    Adafruit_FXAS21002C gyro = null;
    Adafruit_FXOS8700CQ accell = null;
    I2cDevice devGyro = null;
    I2cDevice devAccel = null;

    Gpio GpioGyro = null;
    Gpio GpioAccel = null;

/*    AtomicInteger _gyroIntTriggered;
    AtomicInteger _accelIntTriggered;

    private GpioCallback GyroCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            _gyroIntTriggered.incrementAndGet();
            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.w("GpioCallback", gpio + ": Error event " + error);
        }
    };


    private GpioCallback AccelCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            _accelIntTriggered.incrementAndGet();
            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.d("GpioCallback", gpio + ": Error event " + error);
        }
    };*/

    public void Init() {
/*        _gyroIntTriggered = new AtomicInteger(0);
        _accelIntTriggered = new AtomicInteger(0);*/

        PeripheralManagerService service = new PeripheralManagerService();
        Log.d("MainActivity", "Available GPIO: " + service.getGpioList());

        Log.d("MainActivity", "I2cBusList: " + service.getI2cBusList());
        Log.d("MainActivity", "I2sDeviceList: " + service.getI2sDeviceList());
        Log.d("MainActivity", "PwmList: " + service.getPwmList());
        Log.d("MainActivity", "SpiBusList: " + service.getSpiBusList());
        Log.d("MainActivity", "UartDeviceList: " + service.getUartDeviceList());

/*        try {
            GpioGyro = service.openGpio(GyroInterruptPin);
            // Initialize the pin as an input
            GpioGyro.setDirection(Gpio.DIRECTION_IN);
            // Low voltage is considered active
            GpioGyro.setActiveType(Gpio.ACTIVE_HIGH);
            // Register for all state changes
            GpioGyro.setEdgeTriggerType(Gpio.EDGE_FALLING);
            GpioGyro.registerGpioCallback(GyroCallback);

            GpioAccel = service.openGpio(AccelInterruptPin);
            GpioAccel.setDirection(Gpio.DIRECTION_IN);
            GpioAccel.setActiveType(Gpio.ACTIVE_HIGH);
            // Register for all state changes
            GpioAccel.setEdgeTriggerType(Gpio.EDGE_FALLING);
            GpioAccel.registerGpioCallback(AccelCallback);

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            devGyro = service.openI2cDevice(I2CInterface, Adafruit_FXAS21002C.FXAS21002C_ADDRESS);
            gyro = new Adafruit_FXAS21002C(devGyro);
            gyro.SetupRange(Adafruit_FXAS21002C.RangeEnum.DPS250);
            gyro.SetupODR(Adafruit_FXAS21002C.ODREnum.ODR_50);
            gyro.SetupFifo(Adafruit_FXAS21002C.FifoModeEnum.Disabled, 0);
            //gyro.SetupInterrupts(2, false, 2, false, 1, true, 0, 0);
            gyro.SetupWrapToOne(true);
            //gyro.SetupFifo(Adafruit_FXAS21002C.FifoModeEnum.CircularBuffer, 16);

            gyro.begin();

            devAccel = service.openI2cDevice(I2CInterface, Adafruit_FXOS8700CQ.FXOS8700CQ_ADDRESS);
            accell = new Adafruit_FXOS8700CQ(devAccel);
            accell.SetupFastReadMode(false);
            accell.SetupFullScaleRange(true);
            accell.SetupFifo(Adafruit_FXOS8700CQ.FifoModeEnum.Disabled, 0);
            // 100 Hz ODR in Accell mode, 50 Hz in Hybrid mode
            accell.SetupODR(Adafruit_FXOS8700CQ.ODREnum.ODR_100);
            accell.SetupRange(Adafruit_FXOS8700CQ.AccelRangeEnum.RANGE_2G);
            accell.SetupAutoSleep(false);
            accell.SetupWakeModeOSR(Adafruit_FXOS8700CQ.AccelOSREnum.HighResolution);
            accell.SetupSleepModeOSR(Adafruit_FXOS8700CQ.AccelOSREnum.HighResolution);

            //accell.SetupInterruptControl(false, false, false, false, false, false, 0, 0);
            //accell.SetupInterruptEnable(false, false, false, false, false, false, false, true);
            //accell.SetupInterruptRouting(2, 2, 2, 2, 2, 2, 2, 1);

            accell.SetupOperatingMode(Adafruit_FXOS8700CQ.OperatingModeEnum.HybridMode);
            accell.SetupMagnWakeOSR(3);
            accell.SetupMagnSleepOSR(0);
            accell.SetupMagnHybridAutoincMode(true);
            //accell.SetupMagnSensorDegausFreq(Adafruit_FXOS8700CQ.MagnSensorDegausFreqEnum.Disabled);

            accell.begin();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void Close() {
        if (GpioGyro != null) {
            try {
                GpioGyro.close();
                GpioGyro = null;
            } catch (IOException e) {
                Log.w("MainActivity", "Unable to close GPIO", e);
            }
        }

        if (GpioAccel != null) {
            try {
                GpioAccel.close();
                GpioAccel = null;
            } catch (IOException e) {
                Log.w("MainActivity", "Unable to close GPIO", e);
            }
        }

        // Step 5. Close the resource.
        if (devGyro != null) {
            try {
                devGyro.close();
            } catch (IOException e) {
                Log.e("DEVICE", "Error on PeripheralIO API", e);
            }
            devGyro = null;
        }
        if (devAccel != null) {
            try {
                devAccel.close();
            } catch (IOException e) {
                Log.e("DEVICE", "Error on PeripheralIO API", e);
            }
            devAccel = null;
        }
    }

    public class IMUEvent {
        public SensorEvent gyro;
        public SensorEvent accel;
        public SensorEvent magn;

        public IMUEvent() {
            gyro = null;
            accel = null;
            magn = null;
        }

        public boolean isPopulated() {
            if (gyro == null)
                return false;
            if (accel == null)
                return false;
            return true;
        }
    }

    IMUEvent imuEvent;
    ArrayList<IMUEvent> eventsList;

    @Override
    public void run() {
        DatastoreAndTransmission store = new DatastoreAndTransmission();
        StepDetector stepDetector = new StepDetector();

        try {
            gyro.SetPowerState(Adafruit_FXAS21002C.PowerStateEnum.Active);
            accell.SetPowerState(Adafruit_FXOS8700CQ.PowerStateEnum.Active);
        } catch (IOException e) {
            e.printStackTrace();
        }
        eventsList = new ArrayList<IMUEvent>();
        imuEvent = new IMUEvent();

        try {
            while (Thread.currentThread().isInterrupted() == false) {
/*                if (_gyroIntTriggered.get() > 0) {
                    _gyroIntTriggered.decrementAndGet();
//                    Log.d("Gyro", "interrupt triggered " + System.currentTimeMillis());
*/
                    try {
                        /*int flag = gyro.getIntSourceFlag();
                        boolean dataReady = gyro.IsDataReadyEventSourceFlag(flag);*/
                        int drStatus = gyro.getDRStatus();
                        if (gyro.IsXYZNewDataReady(drStatus)) {
                            imuEvent.gyro = gyro.getEvent();

                            if (imuEvent.isPopulated()) {
                                eventsList.add(imuEvent);
                                imuEvent = new IMUEvent();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
/*                }

                if (_accelIntTriggered.get() > 0) {
                    _accelIntTriggered.decrementAndGet();
//                    Log.d("Accel", "interrupt triggered " + System.currentTimeMillis());
*/
                    try {
/*                        int flag = accell.getIntSourceFlag();
                        int flagMagn = accell.getMagnIntSourceFlag();*/
                        int drStatus = accell.getFStatus();
                        int mdrStatus = accell.getMDRStatus();

                        if ((accell.IsXYZNewDataReady(drStatus)) | (accell.IsMagnXYZNewDataReady(mdrStatus))) {
                            imuEvent.accel = new SensorEvent();
                            imuEvent.magn = new SensorEvent();
                            accell.getEvent(imuEvent.accel, imuEvent.magn);

                            SensorsVector accVals = imuEvent.accel.getAcceleration();
                            stepDetector.NewAccelerometerReading(imuEvent.accel.timestamp, accVals.getX(), accVals.getY(), accVals.getZ());

                            if (imuEvent.isPopulated()) {
                                eventsList.add(imuEvent);
                                imuEvent = new IMUEvent();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                }

                // TODO: Make it 10000 after testing.
                if (eventsList.size() >= 200) {
                    Log.d("Store", "Storing..." + String.valueOf(System.currentTimeMillis()));
                    Log.d("Stepper", "Steps: " + String.valueOf(stepDetector.GetCompletedSteps()));

                    Thread storeThread = new Thread(store);
                    storeThread.setPriority(Thread.MIN_PRIORITY);

                    ArrayList<IMUEvent> tempList = eventsList;
                    eventsList = new ArrayList<IMUEvent>();

                    store.SetList(tempList);
                    storeThread.start();
                }

/*                try {
                    //Thread.sleep(0, 10000);
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt(); // very important
                    break;
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
