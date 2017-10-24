package com.kaliviotis.efthymios.cowsensor.sensorapp;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;

import junit.framework.Assert;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Efthymios on 10/3/2017.
 */

public class Adafruit_FXAS21002C extends Adafruit_Sensor {
    public static int FXAS21002C_ADDRESS = 0x21; // 0100001
    public static int FXAS21002C_ID = 0xD7;      // 1101 0111
    private static float GYRO_SENSITIVITY_250DPS  = 0.0078125F; // Table 35 of datasheet
    private static float GYRO_SENSITIVITY_500DPS  = 0.015625F;  // ..
    private static float GYRO_SENSITIVITY_1000DPS = 0.03125F;   // ..
    private static float GYRO_SENSITIVITY_2000DPS = 0.0625F;    // ..

    public enum RegistersEnum {
        STATUS(0x00),
        OUT_X_MSB(0x01),
        OUT_X_LSB(0x02),
        OUT_Y_MSB(0x03),
        OUT_Y_LSB(0x04),
        OUT_Z_MSB(0x05),
        OUT_Z_LSB(0x06),
        DR_STATUS(0x07),
        F_STATUS(0x08),
        F_SETUP(0x09),
        F_EVENT(0x0A),
        INT_SOURCE_FLAG(0x0B),
        WHO_AM_I(0x0C),    // 11010111   r
        CTRL_REG0(0x0D),   // 00000000   r/w
        CTRL_REG1(0x13),   // 00000000   r/w
        CTRL_REG2(0x14),   // 00000000   r/w
        CTRL_REG3(0x15);

        public final int register;

        RegistersEnum(int code) {
            this.register = code;
        }
    }

    public enum RangeEnum {
        DPS250(3),
        DPS500(2),
        DPS1000(1),
        DPS2000(0);

        public  final int range;

        RangeEnum(int range) {
            this.range = range;
        }
    }

    public enum ODREnum {
        ODR_800(0),
        ODR_400(1),
        ODR_200(2),
        ODR_100(3),
        ODR_50(4),
        ODR_25(5),
        ODR_12_5(6),
        ODR_12_5_1(7);

        public  final int ODR;

        ODREnum(int ODR) {
            this.ODR = ODR;
        }
    }

    public enum FifoModeEnum {
        Disabled(0x00),
        CircularBuffer(0x01),
        StopMode(0x02);

        public final int mode;
        FifoModeEnum(int mode) {
            this.mode = mode;
        }

    }

    public enum PowerStateEnum {
        StandBy(0),
        Ready(1),
        Active(2);

        public  final int state;

        PowerStateEnum(int state) {
            this.state = state;
        }
    }

    public class GyroRawData {
        short x;
        short y;
        short z;
    }

    private FifoModeEnum _fifoMode;
    private int _waterMark;

    private int _cutOff;
    private int _fourWireMode;
    private int _highPassFilterCutoff;
    private int _highPassFilterEnable;
    private RangeEnum _range;
    private ODREnum _ODR;

    private int _interruptConfiguration;

    // CTRL_REG3 (0x15)
    private int _wraptToOne;
    private int _extCtrlen;
    private int _fs_Double;


//    private int _sensorID = -1;
    GyroRawData raw = new GyroRawData();

    private I2cDevice _device;

    public Adafruit_FXAS21002C(I2cDevice device) {
        raw.x = 0;
        raw.y = 0;
        raw.z = 0;

//        _sensorID = sensorID;
        _device = device;
        _fifoMode = FifoModeEnum.Disabled;
        _waterMark = 0;

        _cutOff = 0;
        _fourWireMode = 0;
        _highPassFilterCutoff = 0;
        _highPassFilterEnable = 0;
        _range = RangeEnum.DPS250;

        _ODR = ODREnum.ODR_100;

        _interruptConfiguration = 0;

        // CTRL_REG3 (0x15)
        _wraptToOne = 0;
        _extCtrlen = 0;
        _fs_Double = 0;
    }

    // Needs to be called before begin().
    public void SetupFifo(FifoModeEnum fifoMode, int watermark) {
        _fifoMode = fifoMode;
        _waterMark = watermark;
    }

    /* Register CTRL_REG0 (0x0D) functions - START */
    // CutOff values 0, 1, 2
    public void SetupCutOffFreq(int CutOff) {
        _cutOff = CutOff;
    }

    public void SetupSPIInterfaceMode(boolean FourWireMode) {
        if (FourWireMode)
            _fourWireMode = 0;
        else
            _fourWireMode = 1; // three wires SPI interface.
    }

    public void SetupHighPassFilter(boolean Enable, int CutOff) {
        if (Enable)
            _highPassFilterEnable = 1;
        else
            _highPassFilterEnable = 0;

        _highPassFilterCutoff = CutOff;
    }

    public void SetupRange(RangeEnum range) {
        _range = range;
    }
    /* Register CTRL_REG0 (0x0D) functions - END */

    /* Register CTRL_REG2 (0x14) functions - START */
    public void SetupInterrupts(
            int fifoIntPin,
            boolean fifoIntEnable,
            int rateThresholdIntPin,
            boolean rateThresholdIntEnable,
            int dataReadyIntPin,
            boolean dataReadyIntEnable,
            int interruptLogicPolarity,
            int outputDriverConfiguration
    ) {
        Assert.assertTrue("fifoIntPin should be either 1 or 2", (fifoIntPin >= 1) & (fifoIntPin <= 2));
        Assert.assertTrue("fifoIntPin should be either 1 or 2", (fifoIntPin >= 1) & (fifoIntPin <= 2));
        Assert.assertTrue("rateThresholdIntPin should be either 1 or 2", (rateThresholdIntPin >= 1) & (rateThresholdIntPin <= 2));
        Assert.assertTrue("dataReadyIntPin should be either 1 or 2", (dataReadyIntPin >= 1) & (dataReadyIntPin <= 2));
        Assert.assertTrue("interruptLogicPolarity should be either 0 or 1", (interruptLogicPolarity >= 0) & (interruptLogicPolarity <= 1));
        Assert.assertTrue("outputDriverConfiguration should be either 0 or 1", (outputDriverConfiguration >= 0) & (outputDriverConfiguration <= 1));

        _interruptConfiguration =
                ((2 - fifoIntPin) << 7)
                        | ((fifoIntEnable ? 1 : 0) << 6)
                        | ((2 - rateThresholdIntPin) << 5)
                        | ((rateThresholdIntEnable ? 1 : 0) << 4)
                        | ((2 - dataReadyIntPin) << 3)
                        | ((dataReadyIntEnable ? 1 : 0) << 2)
                        | (interruptLogicPolarity << 1)
                        | outputDriverConfiguration;
    }
    /* Register CTRL_REG2 (0x14) functions - END */

    /* Register CTRL_REG3 (0x15) functions - START */
    public void SetupWrapToOne(boolean enable) { _wraptToOne = enable ? 1 : 0; }
    public void SetupExtCtrlen(boolean enable) { _extCtrlen = enable ? 1 : 0; }
    public void SetupFsDouble(boolean enable) { _fs_Double = enable ? 1 : 0; }
    /* Register CTRL_REG3 (0x15) functions - END */

    public void SetupODR(ODREnum ODR) {
        _ODR = ODR;
    }

    private void write8(int reg, int value) throws IOException {
        _device.writeRegByte(reg, (byte)(value & 0xFF));
    }

    private int read8(int reg) throws IOException {
        return (_device.readRegByte(reg) & 0xFF );
    }

    /* Register DRStatus (0x07) functions - START */
    public int getDRStatus() throws IOException {
        return read8(RegistersEnum.DR_STATUS.register);
    }
    public boolean IsXYZOverwritten(int mdrstatus) { return ((mdrstatus & 0x80) != 0); }
    public boolean IsZOverwritten(int mdrstatus) { return ((mdrstatus & 0x40) != 0); }
    public boolean IsYOverwritten(int mdrstatus) { return ((mdrstatus & 0x20) != 0); }
    public boolean IsXOverwritten(int mdrstatus) { return ((mdrstatus & 0x10) != 0); }
    public boolean IsXYZNewDataReady(int mdrstatus) { return ((mdrstatus & 0x08) != 0); }
    public boolean IsZNewDataReady(int mdrstatus) { return ((mdrstatus & 0x04) != 0); }
    public boolean IsYNewDataReady(int mdrstatus) { return ((mdrstatus & 0x02) != 0); }
    public boolean IsXNewDataReady(int mdrstatus) { return ((mdrstatus & 0x01) != 0); }
    /* Register DRStatus (0x07) functions - END */

    /* Register FStatus (0x08) functions - START */
    public int getFStatus() throws IOException {
        return read8(RegistersEnum.F_STATUS.register);
    }

    public boolean IsFifoOverflow(int f_status) {
        return ((f_status & 0x80) != 0);
    }

    public boolean IsWatermarkEvent(int f_status) {
        return ((f_status & 0x40) != 0);
    }

    public int getFifoSamplesStored(int f_status) {
        return (f_status & 0x3F);
    }
    /* Register FStatus (0x08) functions - END */

    /* Register FEvent (0x0A) functions - START */
    public int getFEvent() throws IOException {
        return read8(RegistersEnum.F_EVENT.register);
    }

    public boolean IsFEventDetected(int f_event) {
        return ((f_event & 0x20) != 0);
    }

    public int getODRPeriodsElapsedSinceFEvent(int f_event) {
        return(f_event & 0x1F);
    }
    /* Register FEvent (0x0A) functions - END */


    /* Register INT_SOURCE_FLAG (0x0B) functions - START */
    public int getIntSourceFlag() throws IOException {
        return read8(RegistersEnum.INT_SOURCE_FLAG.register);
    }

    public boolean IsBootEnded(int intSourceFlag) {
        return ((intSourceFlag & 8) != 0);
    }

    public boolean IsFifoEventSourceFlag(int intSourceFlag) {
        return ((intSourceFlag & 4) != 0);
    }

    public boolean IsRateThresholdSourceFlag(int intSourceFlag) {
        return ((intSourceFlag & 2) != 0);
    }

    public boolean IsDataReadyEventSourceFlag(int intSourceFlag) {
        return ((intSourceFlag & 1) != 0);
    }
    /* Register INT_SOURCE_FLAG (0x0B) functions - END */

    public void ResetDevice() throws IOException {
        try {
            write8(RegistersEnum.CTRL_REG1.register, 0x00);
            write8(RegistersEnum.CTRL_REG1.register, 1 << 6);
        } catch (IOException e) {
//            write8(RegistersEnum.CTRL_REG1.register, 2); // Activate it

            WaitDeviceToReset();
        }
        Log.d("Adafruit_FXAS21002C", "Finished rebooting...");
    }

    private int GetWhoAmI() throws IOException {
/*        int oldReg = read8(RegistersEnum.F_SETUP.register);

        write8(RegistersEnum.F_SETUP.register, 0x00);
        write8(RegistersEnum.F_SETUP.register, (1 << 6) + 1);

        int reg1 = read8(RegistersEnum.CTRL_REG1.register);
        assert (reg1 & 2) == 0 : "Device is not in ACTIVE state";*/

        int ret = read8(RegistersEnum.WHO_AM_I.register);

        //write8(RegistersEnum.F_SETUP.register, oldReg);
        return ret;
    }

    public void WaitDeviceToReset() throws IOException {
        int IntSourceFlag = getIntSourceFlag();
        try {
            while (!IsBootEnded(IntSourceFlag)) {
                Log.d("MainActivity", "Not ready");
                    Thread.sleep(1);
                IntSourceFlag = getIntSourceFlag();
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            return;
        }
        // Set the power state of the device to stand-by.
        SetPowerState(PowerStateEnum.StandBy);
    }

    public void SetPowerState(PowerStateEnum state) throws IOException {
/*        int reg1 = (_ODR.ODR << 2) | state.state;
        write8(RegistersEnum.CTRL_REG1.register, reg1);*/
        int reg1 = read8(RegistersEnum.CTRL_REG1.register);
        reg1 = (reg1 & 0xFC) | state.state;
        write8(RegistersEnum.CTRL_REG1.register, reg1);
    }

    public Boolean begin() throws IOException {
        /* Clear the raw sensor data */
        raw.x = 0;
        raw.y = 0;
        raw.z = 0;

        /* Set CTRL_REG1 (0x13)
           ====================================================================
           BIT  Symbol    Description                                   Default
           ---  ------    --------------------------------------------- -------
             6  RESET     Reset device on 1                                   0
             5  ST        Self test enabled on 1                              0
           4:2  DR        Output data rate                                  000
                          000 = 800 Hz
                          001 = 400 Hz
                          010 = 200 Hz
                          011 = 100 Hz
                          100 = 50 Hz
                          101 = 25 Hz
                          110 = 12.5 Hz
                          111 = 12.5 Hz
             1  ACTIVE    Standby(0)/Active(1)                                0
             0  READY     Standby(0)/Ready(1)                                 0
        */

        /* Make sure we have the correct chip ID since this checks
         for correct address and that the IC is properly connected */
        int id = GetWhoAmI();
        if (id != FXAS21002C_ID)
            return false;

        ResetDevice();
//        SetPowerState(PowerStateEnum.Active);

        SetPowerState(PowerStateEnum.Ready);


        //id = read8(RegistersEnum.WHO_AM_I.register);

        // Set ODR and bring device to stand-by to continue setup.
        int reg1 = (_ODR.ODR << 2);
        write8(RegistersEnum.CTRL_REG1.register, reg1);

        /* Set the F_SETUP register */
        int fsetup = _fifoMode.mode;
        fsetup = (fsetup << 6) | _waterMark;

        // Clear FIFO
        write8(RegistersEnum.F_SETUP.register, 0x00);
        // Set FIFO mode
        write8(RegistersEnum.F_SETUP.register, fsetup);

        write8(RegistersEnum.F_STATUS.register, 0x00);
        /* Set CTRL_REG0 (0x0D)

         */
        int reg0 = 0;
/*        switch(_range)
        {
            case DPS250:
                reg0 = 3;
                break;
            case DPS500:
                reg0 = 2;
                break;
            case DPS1000:
                reg0 = 1;
                break;
            case DPS2000:
                reg0 = 0;
                break;
        }*/
        reg0 = _range.range
                | (_cutOff << 6)
                | (_fourWireMode << 5)
                | (_highPassFilterCutoff << 3)
                | (_highPassFilterEnable << 2);
        write8(RegistersEnum.CTRL_REG0.register, reg0);

        write8(RegistersEnum.CTRL_REG2.register, _interruptConfiguration);

        int reg3 =
                (_wraptToOne << 3)
                | (_extCtrlen << 2)
                | _fs_Double;
        write8(RegistersEnum.CTRL_REG3.register, reg3);

        // Interupt enable
        /*write8(RegistersEnum.CTRL_REG2.register, 0xC0);
        write8(RegistersEnum.CTRL_REG3.register, 8);*/




        // Bring the device in 'Active' mode.
/*        reg1 = (_ODR.ODR << 2) | 2;
        write8(RegistersEnum.CTRL_REG1.register, reg1);*/

        //SetPowerState(PowerStateEnum.Active);

/*        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return true;
    }

    public SensorEvent getEvent() throws IOException {
        SensorEvent event = new SensorEvent();

        /* Clear the raw data placeholder */
        raw.x = 0;
        raw.y = 0;
        raw.z = 0;

        event.version = 0;
        event.sensor_id = FXAS21002C_ID;
        event.type = SensorsType.SENSOR_TYPE_GYROSCOPE.sensorType;
        event.timestamp = System.currentTimeMillis();

        /* Read 7 bytes from the sensor */
        byte[] buff = new byte[6];
        _device.readRegBuffer(RegistersEnum.OUT_X_MSB.register, buff, 6);
/*        _device.readRegBuffer(RegistersEnum.STATUS.register, buff, 7);
        byte status = buff[0];
        byte xhi = buff[1];
        byte xlo = buff[2];
        byte yhi = buff[3];
        byte ylo = buff[4];
        byte zhi = buff[5];
        byte zlo = buff[6];*/

        /* Assign raw values in case someone needs them */
        /* Shift values to create properly formed integer */
        raw.x = (short)(((buff[0] & 0xFF) << 8) | (buff[1] & 0xFF));
        raw.y = (short)(((buff[2] & 0xFF) << 8) | (buff[3] & 0xFF));
        raw.z = (short)(((buff[4] & 0xFF) << 8) | (buff[5] & 0xFF));

        float gyroX = raw.x;
        float gyroY = raw.y;
        float gyroZ = raw.z;

        /* Compensate values depending on the resolution */
        switch(_range)
        {
            case DPS250:
                gyroX *= GYRO_SENSITIVITY_250DPS;
                gyroY *= GYRO_SENSITIVITY_250DPS;
                gyroZ *= GYRO_SENSITIVITY_250DPS;
                break;
            case DPS500:
                gyroX *= GYRO_SENSITIVITY_500DPS;
                gyroY *= GYRO_SENSITIVITY_500DPS;
                gyroZ *= GYRO_SENSITIVITY_500DPS;
                break;
            case DPS1000:
                gyroX *= GYRO_SENSITIVITY_1000DPS;
                gyroY *= GYRO_SENSITIVITY_1000DPS;
                gyroZ *= GYRO_SENSITIVITY_1000DPS;
                break;
            case DPS2000:
                gyroX *= GYRO_SENSITIVITY_2000DPS;
                gyroY *= GYRO_SENSITIVITY_2000DPS;
                gyroZ *= GYRO_SENSITIVITY_2000DPS;
                break;
        }
        if (_fs_Double == 1) {
            gyroX *= 2;
            gyroY *= 2;
            gyroZ *= 2;
        }

/*        gyroX *= SENSORS_DPS_TO_RADS;
        gyroY *= SENSORS_DPS_TO_RADS;
        gyroZ *= SENSORS_DPS_TO_RADS;*/

        SensorsVector gyro = event.getGyro();
        gyro.setVector(gyroX, gyroY, gyroZ);
//        gyro.status = (status & 0xFF);

        return event;
    }

    public ArrayList<SensorDetails> getSensor() {
        ArrayList<SensorDetails> lst = new ArrayList<SensorDetails>();

        SensorDetails sensor = new SensorDetails();
        sensor.name = "FXAS21002C";
        sensor.version = 1;
        sensor.sensor_id = FXAS21002C_ID;
        sensor.type = SensorsType.SENSOR_TYPE_GYROSCOPE;
        sensor.min_delay = 0;
        switch (_range) {
            case DPS250:
                sensor.max_value = 250;
                sensor.min_value = -250;
                sensor.resolution = GYRO_SENSITIVITY_250DPS;
                break;
            case DPS500:
                sensor.max_value = 500;
                sensor.min_value = -500;
                sensor.resolution = GYRO_SENSITIVITY_500DPS;
                break;
            case DPS1000:
                sensor.max_value = 1000;
                sensor.min_value = -1000;
                sensor.resolution = GYRO_SENSITIVITY_1000DPS;
                break;
            case DPS2000:
                sensor.max_value = 2000;
                sensor.min_value = -2000;
                sensor.resolution = GYRO_SENSITIVITY_2000DPS;
                break;
        }
        switch (_ODR) {
            case ODR_800:
                sensor.min_delay = 1000000 / 800;
                break;
            case ODR_400:
                sensor.min_delay = 1000000 / 400;
                break;
            case ODR_200:
                sensor.min_delay = 1000000 / 200;
                break;
            case ODR_100:
                sensor.min_delay = 1000000 / 100;
                break;
            case ODR_50:
                sensor.min_delay = 1000000 / 50;
                break;
            case ODR_25:
                sensor.min_delay = 1000000 / 25;
                break;
            case ODR_12_5:
                sensor.min_delay = (int) (1000000 / 12.5);
                break;
            case ODR_12_5_1:
                sensor.min_delay = (int) (1000000 / 12.5);
                break;
        }

        lst.add(sensor);

        return lst;
    }

}
