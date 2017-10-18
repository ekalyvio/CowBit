package com.kaliviotis.efthymios.cowsensor;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;

import junit.framework.Assert;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Efthymios on 10/9/2017.
 */

public class Adafruit_FXOS8700CQ extends Adafruit_Sensor {
    public static int FXOS8700CQ_ADDRESS = 0x1F; // 0001 FFFF
    public static int FXOS8700CQ_ID = 0xC7;      // 1100 0111
    private static int SENSITIVITY_2G = 4096; // 0.000244140625F; // 1 / 4096
    private static int SENSITIVITY_4G = 2048; // 0.00048828125F;  // 1 / 2048
    private static int SENSITIVITY_8G = 1024; // 0.0009765625F;   // 1 / 1024
    private static int SENSITIVITY_MAG = 10; // 0.1F;


    public enum RegistersEnum
    {                                             // DEFAULT    TYPE
        STATUS(0x00),
        OUT_X_MSB(0x01),
        OUT_X_LSB(0x02),
        OUT_Y_MSB(0x03),
        OUT_Y_LSB(0x04),
        OUT_Z_MSB(0x05),
        OUT_Z_LSB(0x06),
        F_SETUP(0x09),
        TRIG_CFG(0x0A),
        SYS_MODE(0X0B),

        INT_SOURCE(0x0C),
        WHO_AM_I(0x0D),
        XYZ_DATA_CFG(0x0E),
        HP_FILTER_CUTOFF(0x0F),
        PL_STATUS(0x10),
        PL_CFG(0x11),
        PL_COUNT(0x12),
        PL_BF_ZCOMP(0x13),
        PL_THS_REG(0x14),
        A_FFMT_CFG(0x15),
        A_FFMT_SRC(0x16),
        A_FFMT_THS(0x17),
        A_FFMT_COUNT(0x18),
        TRANSIENT_CFG(0x1D),
        TRANSIENT_SRC(0x1E),
        TRANSIENT_THS(0x1F),
        TRANSIENT_COUNT(0x20),
        PULSE_CFG(0x21),
        PULSE_SRC(0x22),
        PULSE_THSX(0x23),
        PULSE_THSY(0x24),
        PULSE_THSZ(0x25),

        PULSE_TMLT(0x26),
        PULSE_LTCY(0x27),
        PULSE_WIND(0x28),
        ASLP_COUNT(0x29),
        CTRL_REG1(0x2A),
        CTRL_REG2(0x2B),
        CTRL_REG3(0x2C),
        CTRL_REG4(0x2D),
        CTRL_REG5(0x2E),
        OFF_X(0x2F),
        OFF_Y(0x30),
        OFF_Z(0x31),
        M_DR_STATUS(0x32),
        M_OUT_X_MSB(0x33),
        M_OUT_X_LSB(0x34),
        M_OUT_Y_MSB(0x35),
        M_OUT_Y_LSB(0x36),
        M_OUT_Z_MSB(0x37),
        M_OUT_Z_LSB(0x38),
        CMP_X_MSB(0x39),
        CMP_X_LSB(0x3A),
        CMP_Y_MSB(0x3B),
        CMP_Y_LSB(0x3C),
        CMP_Z_MSB(0x3D),
        CMP_Z_LSB(0x3E),
        M_OFF_X_MSB(0x3F),
        M_OFF_X_LSB(0x40),
        M_OFF_Y_MSB(0x41),
        M_OFF_Y_LSB(0x42),
        M_OFF_Z_MSB(0x43),
        M_OFF_Z_LSB(0x44),
        MAX_X_MSB(0x45),
        MAX_X_LSB(0x46),
        MAX_Y_MSB(0x47),
        MAX_Y_LSB(0x48),
        MAX_Z_MSB(0x49),
        MAX_Z_LSB(0x4A),
        MIN_X_MSB(0x4B),

        MIN_X_LSB(0x4C),
        MIN_Y_MSB(0x4D),
        MIN_Y_LSB(0x4E),
        MIN_Z_MSB(0x4F),
        MIN_Z_LSB(0x50),
        TEMP(0x51),
        M_THS_CFG(0x52),
        M_THS_SRC(0x53),
        M_THS_X_MSB(0x54),
        M_THS_X_LSB(0x55),
        M_THS_Y_MSB(0x56),
        M_THS_Y_LSB(0x57),
        M_THS_Z_MSB(0x58),
        M_THS_Z_LSB(0x59),
        M_THS_COUNT(0x5A),
        M_CTRL_REG1(0x5B),
        M_CTRL_REG2(0x5C),
        M_CTRL_REG3(0x5D),
        M_INT_SRC(0x5E),
        A_VECM_CFG(0x5F),
        A_VECM_THS_MSB(0x60),
        A_VECM_THS_LSB(0x61),
        A_VECM_CNT(0x62),
        A_VECM_INITX_MSB(0x63),
        A_VECM_INITX_LSB(0x64),
        A_VECM_INITY_MSB(0x65),
        A_VECM_INITY_LSB(0x66),
        A_VECM_INITZ_MSB(0x67),
        A_VECM_INITZ_LSB(0x68),
        M_VECM_CFG(0x69),
        M_VECM_THS_MSB(0x6A),
        M_VECM_THS_LSB(0x6B),
        M_VECM_CNT(0x6C),

        M_VECM_INITX_MSB(0x6D),
        M_VECM_INITX_LSB(0x6E),
        M_VECM_INITY_MSB(0x6F),
        M_VECM_INITY_LSB(0x70),
        M_VECM_INITZ_MSB(0x71),
        M_VECM_INITZ_LSB(0x72),
        A_FFMT_THS_X_MSB(0x73),
        A_FFMT_THS_X_LSB(0x74),
        A_FFMT_THS_Y_MSB(0x75),
        A_FFMT_THS_Y_LSB(0x76),
        A_FFMT_THS_Z_MSB(0x77),
        A_FFMT_THS_Z_LSB(0x78);

        public final int register;

        RegistersEnum(int code) {
            this.register = code;
        }
    };

    public enum AccelRangeEnum
    {
        RANGE_2G(0x00),
        RANGE_4G(0x01),
        RANGE_8G(0x02);

        public final int range;

        AccelRangeEnum(int range) {
            this.range = range;
        }
    };

    public enum ODREnum {
        ODR_800(0),
        ODR_400(1),
        ODR_200(2),
        ODR_100(3),
        ODR_50(4),
        ODR_12_5(5),
        ODR_06_25(6),
        ODR_01_56(6);

        public final int ODR;

        ODREnum(int ODR) {
            this.ODR = ODR;
        }
    }

    public enum ASLPRateEnum {
        ASLP_50(0),
        ASLP_12_5(1),
        ASLP_06_25(2),
        ASLP_01_56(3);

        public final int rate;

        ASLPRateEnum(int rate) {
            this.rate = rate;
        }
    }

    public enum AccelOSREnum {
        Normal(0),
        LowNoiseLowPower(1),
        HighResolution(2),
        LowPower(3);

        public final int osr;

        AccelOSREnum(int osr) { this.osr = osr; }
    }

    public enum FifoModeEnum {
        Disabled(0x00),
        CircularBuffer(0x01),
        StopMode(0x02),
        TrigerMode(0x03);

        public final int mode;
        FifoModeEnum(int mode) {
            this.mode = mode;
        }
    }

    public enum PowerStateEnum {
        StandBy(0),
        Active(1);

        public final int state;

        PowerStateEnum(int state) {
            this.state = state;
        }
    }

    public enum OperatingModeEnum {
        OnlyAccelerometer(0),
        OnlyMagnetometer(1),
        HybridMode(3);

        public final int mode;

        OperatingModeEnum(int mode) { this.mode = mode; }
    }

    public enum MagnSensorDegausFreqEnum {
        BeginingOfEachODR(0),
        Every16ODR(1),
        Every512ODR(2),
        Disabled(3);

        public final int freq;

        MagnSensorDegausFreqEnum(int freq) { this.freq = freq; }
    }

    public class SensorRawData {
        short x;
        short y;
        short z;
    }

    // XYZ_DATA_CFG
    private AccelRangeEnum _range;
    private int _highPassFilterEnable;

    private FifoModeEnum _fifoMode;
    private int _waterMark;
    private ODREnum _ODR;
    private ASLPRateEnum _aslpRate;
    private int _fullScaleRange;
    private int _fastReadMode;

    private AccelOSREnum _sleepModeOSR;
    private AccelOSREnum _wakeModeOSR;
    private int _autosleep;

    // CTRL_REG3
    private int _interruptControl;

    // CTRL_REG4
    private int _interruptEnable;

    // CTRL_REG5
    private int _interruptRouting;

    // M_CTRL_REG1
    private int _magnHardIronOffsetAutocalibration;
    private int _magnOneShotDegauss;
    private int _magnOneShotTriggeredMeasurementMode;
    private int _magnWakeOSR;
    private OperatingModeEnum _operatingModeEnum;

    // M_CTRL_REG2
    private int _mag_hyb_autoinc_mode;
    private int _mag_maxmin_detection_dis;
    private int _mag_maxmin_detection_dis_threshold;
    private MagnSensorDegausFreqEnum _mag_degaus_freq;

    // M_CTRL_REG3
    private int _mag_raw;
    private int _magnSleepOSR;
    private int _magnThresholdXYZUpdate;

    SensorRawData _accelRawData = new SensorRawData();
    SensorRawData _magnRawData = new SensorRawData();

    private I2cDevice _device;

    public Adafruit_FXOS8700CQ(I2cDevice device) {
        _accelRawData.x = 0;
        _accelRawData.y = 0;
        _accelRawData.z = 0;
        _magnRawData.x = 0;
        _magnRawData.y = 0;
        _magnRawData.z = 0;

//        _sensorID = sensorID;
        _device = device;

        _range = AccelRangeEnum.RANGE_2G;
        _highPassFilterEnable = 0;

        _fifoMode = FifoModeEnum.Disabled;
        _waterMark = 0;

        _ODR = ODREnum.ODR_800;
        _aslpRate = ASLPRateEnum.ASLP_50;
        _fullScaleRange = 0;
        _fastReadMode = 0;

        _sleepModeOSR = AccelOSREnum.Normal;
        _wakeModeOSR = AccelOSREnum.Normal;
        _autosleep = 0;

        _interruptControl = 0;
        _interruptEnable = 0;
        _interruptRouting = 0;

        _magnHardIronOffsetAutocalibration = 0;
        _magnOneShotDegauss = 0;
        _magnOneShotTriggeredMeasurementMode = 0;
        _magnWakeOSR = 0;
        _operatingModeEnum = OperatingModeEnum.OnlyAccelerometer;

        _mag_hyb_autoinc_mode = 0;
        _mag_maxmin_detection_dis = 0;
        _mag_maxmin_detection_dis_threshold = 0;
        _mag_degaus_freq = MagnSensorDegausFreqEnum.BeginingOfEachODR;

        _mag_raw = 0;
        _magnSleepOSR = 0;
        _magnThresholdXYZUpdate = 0;
    }

    /* Register XYZ_DATA_CFG (address 0x0E) functions - START */
    public void SetupRange(AccelRangeEnum range) {
        _range = range;
    }
    public void SetupHighPassFilter(boolean enable) { _highPassFilterEnable = enable ? 1 : 0; }
    /* Register XYZ_DATA_CFG (address 0x0E) functions - END */

    /* Register CTRL_REG1 (address 0x2A) functions - START */
    public void SetupODR(ODREnum ODR) {
        _ODR = ODR;
    }
    public void SetupASLPRate(ASLPRateEnum aslp) { _aslpRate = aslp; }
    public void SetupFullScaleRange(boolean FullScaleRange) { _fullScaleRange = FullScaleRange ? 1 : 0; }
    public void SetupFastReadMode(boolean FastReadMode) { _fastReadMode = FastReadMode ? 1 : 0; }
    /* Register CTRL_REG1 (address 0x2A) functions - END */

    /* Register CTRL_REG2 (address 0x2B) functions - START */
    public void SetupSleepModeOSR(AccelOSREnum mode) { _sleepModeOSR = mode; }
    public void SetupWakeModeOSR(AccelOSREnum mode) { _wakeModeOSR = mode; }
    public void SetupAutoSleep(boolean AutoSleep) { _autosleep = AutoSleep ? 1 : 0; }
    /* Register CTRL_REG2 (address 0x2B) functions - END */

    /* Register CTRL_REG3 (address 0x2C) functions - START */
    public void SetupInterruptControl(
            boolean fifo_gate,
            boolean wake_tran,
            boolean wake_lndprt,
            boolean wake_pulse,
            boolean wake_ffmt,
            boolean wake_a_vecm,
            int interruptLogicPolarity,
            int outputDriverConfiguration
    ) {
        Assert.assertTrue("interruptLogicPolarity should be either 0 or 1", (interruptLogicPolarity >= 0) & (interruptLogicPolarity <= 1));
        Assert.assertTrue("outputDriverConfiguration should be either 0 or 1", (outputDriverConfiguration >= 0) & (outputDriverConfiguration <= 1));

        _interruptControl =
                ((fifo_gate ? 1 : 0) << 7)
                | ((wake_tran ? 1 : 0) << 6)
                | ((wake_lndprt ? 1 : 0) << 5)
                | ((wake_pulse ? 1 : 0) << 4)
                | ((wake_ffmt ? 1 : 0) << 3)
                | ((wake_a_vecm ? 1 : 0) << 2)
                | (interruptLogicPolarity << 1)
                | outputDriverConfiguration;
    }
    /* Register CTRL_REG3 (address 0x2C) functions - END */

    /* Register CTRL_REG4 (address 0x2D) functions - START */
    public void SetupInterruptEnable(
            boolean int_en_aslp,
            boolean int_en_fifo,
            boolean int_en_trans,
            boolean int_en_lndprt,
            boolean int_en_pulse,
            boolean int_en_ffmt,
            boolean int_en_a_vecm,
            boolean int_en_drdy
    ) {
        _interruptEnable =
                ((int_en_aslp ? 1 : 0) << 7)
                | ((int_en_fifo ? 1 : 0) << 6)
                | ((int_en_trans ? 1 : 0) << 5)
                | ((int_en_lndprt ? 1 : 0) << 4)
                | ((int_en_pulse ? 1 : 0) << 3)
                | ((int_en_ffmt ? 1 : 0) << 2)
                | ((int_en_a_vecm ? 1 : 0) << 1)
                | (int_en_drdy ? 1 : 0);
    }
    /* Register CTRL_REG4 (address 0x2D) functions - END */

    /* Register CTRL_REG5 (address 0x2E) functions - START */
    public void SetupInterruptRouting(
            int int_cfg_aslp,
            int int_cfg_fifo,
            int int_cfg_trans,
            int int_cfg_lndprt,
            int int_cfg_pulse,
            int int_cfg_ffmt,
            int int_cfg_a_vecm,
            int int_cfg_drdy
    ) {
        Assert.assertTrue("int_cfg_aslp should be either 1 or 2", (int_cfg_aslp >= 1) & (int_cfg_aslp <= 2));
        Assert.assertTrue("int_cfg_fifo should be either 1 or 2", (int_cfg_fifo >= 1) & (int_cfg_fifo <= 2));
        Assert.assertTrue("int_cfg_trans should be either 1 or 2", (int_cfg_trans >= 1) & (int_cfg_trans <= 2));
        Assert.assertTrue("int_cfg_lndprt should be either 1 or 2", (int_cfg_lndprt >= 1) & (int_cfg_lndprt <= 2));
        Assert.assertTrue("int_cfg_pulse should be either 1 or 2", (int_cfg_pulse >= 1) & (int_cfg_pulse <= 2));
        Assert.assertTrue("int_cfg_ffmt should be either 1 or 2", (int_cfg_ffmt >= 1) & (int_cfg_ffmt <= 2));
        Assert.assertTrue("int_cfg_a_vecm should be either 1 or 2", (int_cfg_a_vecm >= 1) & (int_cfg_a_vecm <= 2));
        Assert.assertTrue("int_cfg_drdy should be either 1 or 2", (int_cfg_drdy >= 1) & (int_cfg_drdy <= 2));

        _interruptRouting =
                ((2 - int_cfg_aslp) << 7)
                | ((2 - int_cfg_fifo) << 6)
                | ((2 - int_cfg_trans) << 5)
                | ((2 - int_cfg_lndprt) << 4)
                | ((2 - int_cfg_pulse) << 3)
                | ((2 - int_cfg_ffmt) << 2)
                | ((2 - int_cfg_a_vecm) << 1)
                | (2 - int_cfg_drdy);
    }
    /* Register CTRL_REG5 (address 0x2E) functions - END */

    /* Register M_CTRL_REG1 (address 0x5B) functions - START */
    public void SetupMagnHardIronOffsetAutocalibration(boolean enable) { _magnHardIronOffsetAutocalibration = enable ? 1 : 0; }
    public void SetupMagnOneShotDegauss(boolean enable) { _magnOneShotDegauss = enable ? 1 : 0; }
    public void SetupMagnOneShotTriggeredMeasurementMode(boolean enable) { _magnOneShotTriggeredMeasurementMode = enable ? 1 : 0; }
    public void SetupMagnWakeOSR(int osr) { _magnWakeOSR = osr; }
    public void SetupOperatingMode(OperatingModeEnum mode) { _operatingModeEnum = mode; }
    /* Register M_CTRL_REG1 (address 0x5B) functions - END */

    /* Register M_CTRL_REG2 (address 0x5C) functions - START */
    //private int mag_maxmin_detection_
    public void SetupMagnHybridAutoincMode(boolean enable) { _mag_hyb_autoinc_mode = enable ? 1 : 0; }
    public void SetupMagnMaxMinDetectionDisable(boolean disable) { _mag_maxmin_detection_dis = disable ? 1 : 0; }
    public void SetupMagnMaxMinDetectionDisableThreshold(boolean disable) { _mag_maxmin_detection_dis_threshold = disable ? 1 : 0; }
    public void SetMagnMaxMinReset(boolean reset) throws IOException {
        int mreg2 = read8(RegistersEnum.M_CTRL_REG2.register);
        mreg2 = (mreg2 & 0xFB) | (reset ? 1 : 0);
        write8(RegistersEnum.CTRL_REG1.register, mreg2);
    }
    public void SetupMagnSensorDegausFreq(MagnSensorDegausFreqEnum freq) { _mag_degaus_freq = freq; }
    /* Register M_CTRL_REG2 (address 0x5C) functions - END */

    /* Register M_CTRL_REG3 (address 0x5D) functions - START */
    public void SetupMagnRawMode(boolean enable) { _mag_raw = enable ? 1 : 0; }
    public void SetupMagnSleepOSR(int osr) { _magnSleepOSR = osr; }
    public void SetupMagnThresholdXYZUpdate(boolean OnlyReferenceAxis) { _magnThresholdXYZUpdate = OnlyReferenceAxis ? 1 : 0; }
    /* Register M_CTRL_REG3 (address 0x5D) functions - END */


    // Needs to be called before begin().
    public void SetupFifo(FifoModeEnum fifoMode, int watermark) {
        _fifoMode = fifoMode;
        _waterMark = watermark;
    }

    private void write8(int reg, int value) throws IOException {
        _device.writeRegByte(reg, (byte)(value & 0xFF));
    }

    private int read8(int reg) throws IOException {
        return (_device.readRegByte(reg) & 0xFF );
    }

    public void ResetDevice() throws IOException {
        try {
            write8(RegistersEnum.CTRL_REG1.register, 0x00);
            write8(RegistersEnum.CTRL_REG2.register, 1 << 6);
            Thread.sleep(2);
        } catch (IOException e) {
            WaitDeviceToReset();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("Adafruit_FXOS8700CQ", "Finished rebooting...");
    }

    public void WaitDeviceToReset() throws IOException {
        int ctrlReg2 = read8(RegistersEnum.CTRL_REG2.register);
        try {
            while ((ctrlReg2 & (1 << 6)) != 0) {
                Log.d("MainActivity", "Not ready");
                Thread.sleep(1);
                ctrlReg2 = read8(RegistersEnum.CTRL_REG2.register);
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            return;
        }
    }

    public void SetPowerState(PowerStateEnum state) throws IOException {
        int reg1 = read8(RegistersEnum.CTRL_REG1.register);
        reg1 = (reg1 & 0xFE) | state.state;
        write8(RegistersEnum.CTRL_REG1.register, reg1);
    }

    public int getIntSourceFlag() throws IOException {
        return read8(RegistersEnum.INT_SOURCE.register);
    }

    public int getMagnIntSourceFlag() throws IOException {
        return read8(RegistersEnum.M_INT_SRC.register);
    }


    /* Register Status (0x00) functions - START */
    public int getFStatus() throws IOException {
        return read8(RegistersEnum.STATUS.register);
    }

    // When FIFO is enabled
    public boolean IsFifoOverflow(int status) {
        return ((status & 0x80) != 0);
    }
    public boolean IsWatermarkEvent(int status) {
        return ((status & 0x40) != 0);
    }
    public int getFifoSamplesStored(int status) {
        return (status & 0x3F);
    }

    // When FIFO is disabled
    public boolean IsXYZOverwritten(int status) { return ((status & 0x80) != 0); }
    public boolean IsZOverwritten(int status) { return ((status & 0x40) != 0); }
    public boolean IsYOverwritten(int status) { return ((status & 0x20) != 0); }
    public boolean IsXOverwritten(int status) { return ((status & 0x10) != 0); }
    public boolean IsXYZNewDataReady(int status) { return ((status & 0x08) != 0); }
    public boolean IsZNewDataReady(int status) { return ((status & 0x04) != 0); }
    public boolean IsYNewDataReady(int status) { return ((status & 0x02) != 0); }
    public boolean IsXNewDataReady(int status) { return ((status & 0x01) != 0); }

    /* Register Status (0x00) functions - END */

    /* Register M_DR_STATUS (address 0x32) functions - START */
    public int getMDRStatus() throws IOException {
        return read8(RegistersEnum.STATUS.register);
    }
    public boolean IsMagnXYZOverwritten(int mdrstatus) { return ((mdrstatus & 0x80) != 0); }
    public boolean IsMagnZOverwritten(int mdrstatus) { return ((mdrstatus & 0x40) != 0); }
    public boolean IsMagnYOverwritten(int mdrstatus) { return ((mdrstatus & 0x20) != 0); }
    public boolean IsMagnXOverwritten(int mdrstatus) { return ((mdrstatus & 0x10) != 0); }
    public boolean IsMagnXYZNewDataReady(int mdrstatus) { return ((mdrstatus & 0x08) != 0); }
    public boolean IsMagnZNewDataReady(int mdrstatus) { return ((mdrstatus & 0x04) != 0); }
    public boolean IsMagnYNewDataReady(int mdrstatus) { return ((mdrstatus & 0x02) != 0); }
    public boolean IsMagnXNewDataReady(int mdrstatus) { return ((mdrstatus & 0x01) != 0); }
    /* Register M_DR_STATUS (address 0x32) functions - END */


    private int GetWhoAmI() throws IOException {
/*        int oldReg = read8(RegistersEnum.F_SETUP.register);

        write8(RegistersEnum.F_SETUP.register, 0x00);
        write8(RegistersEnum.F_SETUP.register, (1 << 6) + 1);

        int reg1 = read8(RegistersEnum.CTRL_REG1.register);
        assert (reg1 & 2) == 0 : "Device is not in ACTIVE state";
*/
        int ret = read8(RegistersEnum.WHO_AM_I.register);

//        write8(RegistersEnum.F_SETUP.register, oldReg);
        return ret;
    }


    public Boolean begin() throws IOException {
        /* Clear the raw sensor data */
        _accelRawData.x = 0;
        _accelRawData.y = 0;
        _accelRawData.z = 0;
        _magnRawData.x = 0;
        _magnRawData.y = 0;
        _magnRawData.z = 0;

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
        if (id != FXOS8700CQ_ID)
            return false;

        //SetPowerState(PowerStateEnum.StandBy);
        ResetDevice();

        int reg1 =
                (_aslpRate.rate << 6)
                | (_ODR.ODR << 3)
                | (_fullScaleRange << 2)
                | (_fastReadMode);
        write8(RegistersEnum.CTRL_REG1.register, reg1);

        int reg2 =
                (_sleepModeOSR.osr << 3)
                | (_autosleep << 2)
                | (_wakeModeOSR.osr);
        write8(RegistersEnum.CTRL_REG2.register, reg2);

        int xyzReg =
                (_highPassFilterEnable << 4)
                | _range.range;
        write8(RegistersEnum.XYZ_DATA_CFG.register, xyzReg);

        // Set the F_SETUP register
        int fsetup = _fifoMode.mode;
        fsetup = (fsetup << 6) | _waterMark;

        // Clear FIFO
        write8(RegistersEnum.F_SETUP.register, 0x00);
        // Set FIFO mode
        write8(RegistersEnum.F_SETUP.register, fsetup);

        write8(RegistersEnum.CTRL_REG5.register, _interruptRouting);
        write8(RegistersEnum.CTRL_REG4.register, _interruptEnable);
        write8(RegistersEnum.CTRL_REG3.register, _interruptControl);

        int m_ctrlReg1 =
                (_magnHardIronOffsetAutocalibration << 7)
                | (_magnOneShotDegauss << 6)
                | (_magnOneShotTriggeredMeasurementMode << 5)
                | (_magnWakeOSR << 2)
                | _operatingModeEnum.mode;
        write8(RegistersEnum.M_CTRL_REG1.register, m_ctrlReg1);

        int m_ctrlReg2 =
                (_mag_hyb_autoinc_mode << 5)
                | (_mag_maxmin_detection_dis << 4)
                | (_mag_maxmin_detection_dis_threshold << 3)
                | _mag_degaus_freq.freq;
        write8(RegistersEnum.M_CTRL_REG2.register, m_ctrlReg2);

        int m_ctrlReg3 =
                (_mag_raw << 7)
                | (_magnSleepOSR << 4)
                | (_magnThresholdXYZUpdate << 3);
        write8(RegistersEnum.M_CTRL_REG3.register, m_ctrlReg3);

        // Bring the device in 'Active' mode.
        //reg1 = (_ODR.ODR << 2) | 2;
        //write8(RegistersEnum.CTRL_REG1.register, reg1);
        return true;
    }

    public void getEvent(SensorEvent accel, SensorEvent magn) throws IOException {
        // TODO: Update the application (if needed) in order to support all other of the bellow (Asserted) modes. eg. Make it work with FastReadMode == 0, etc
        Assert.assertTrue("FastReadMode (CTRL_REG1) should be 0", (_fastReadMode == 0));
        Assert.assertTrue("Only HybridMode (M_CTRL_REG1(m_hms)) is currently supported", (_operatingModeEnum == OperatingModeEnum.HybridMode));
        Assert.assertTrue("AutoInc mode (M_CTRL_REG2(hyb_autoinc_mode)) should be enabled", _mag_hyb_autoinc_mode == 1);


        accel.version = 0;
        accel.sensor_id = FXOS8700CQ_ID;
        accel.type = SensorsType.SENSOR_TYPE_ACCELEROMETER.sensorType;
        accel.timestamp = System.currentTimeMillis();

        magn.version = 0;
        magn.sensor_id = FXOS8700CQ_ID;
        magn.type = SensorsType.SENSOR_TYPE_MAGNETIC_FIELD.sensorType;
        magn.timestamp = accel.timestamp;

        SensorRawData acc1 = new SensorRawData();
        SensorRawData acc2 = new SensorRawData();

        byte[] buff2 = new byte[12];
        //_device.readRegBuffer(RegistersEnum.M_OUT_X_MSB.register, buff, 12);
        _device.readRegBuffer(RegistersEnum.M_OUT_X_MSB.register, buff2, 12);
        _accelRawData.x = (short)((((buff2[6] & 0xFF) << 26) | ((buff2[7] & 0xFF) << 18)) >> 18);
        _accelRawData.y = (short)((((buff2[8] & 0xFF) << 26) | ((buff2[9] & 0xFF) << 18)) >> 18);
        _accelRawData.z = (short)((((buff2[10] & 0xFF) << 26) | ((buff2[11] & 0xFF) << 18)) >> 18);

        _magnRawData.x = (short)(((buff2[0] & 0xFF) << 8) | (buff2[1] & 0xFF));
        _magnRawData.y = (short)(((buff2[2] & 0xFF) << 8) | (buff2[3] & 0xFF));
        _magnRawData.z = (short)(((buff2[4] & 0xFF) << 8) | (buff2[5] & 0xFF));

        //Log.d("VALUES", String.format("%d %d %d - %d %d %d ---- %f", _accelRawData.x, _accelRawData.y, _accelRawData.z, _magnRawData.x, _magnRawData.y, _magnRawData.z, heading));

        int sensit = 0;
        switch (_range) {
            case RANGE_2G:
                sensit = SENSITIVITY_2G;
                break;
            case RANGE_4G:
                sensit = SENSITIVITY_4G;
                break;
            case RANGE_8G:
                sensit = SENSITIVITY_8G;
                break;
        }

        float accelX = (float)_accelRawData.x / sensit;
        float accelY = (float)_accelRawData.y / sensit;
        float accelZ = (float)_accelRawData.z / sensit;

        SensorsVector accEvt = accel.getGyro();
        accEvt.setVector(accelX, accelY, accelZ);
//        accEvt.status = 0;

        float magnX = (float)_magnRawData.x / SENSITIVITY_MAG;
        float magnY = (float)_magnRawData.y / SENSITIVITY_MAG;
        float magnZ = (float)_magnRawData.z / SENSITIVITY_MAG;

//        Log.d("VALUES", String.format("%f %f %f", magnX, magnY, magnZ));

        SensorsVector magEvt = magn.getGyro();
        magEvt.setVector(magnX, magnY, magnZ);
//        magEvt.status = 0;

        // Calculate the angle of the vector y,x
//        double heading = (atan2(acc2.y,acc2.x) * 180) / Math.PI;
    }

    public ArrayList<SensorDetails> getSensor() {
        ArrayList<SensorDetails> lst = new ArrayList<SensorDetails>();

        SensorDetails sensor = new SensorDetails();
        sensor.name = "FXOS8700CQ";
        sensor.version = 1;
        sensor.sensor_id = FXOS8700CQ_ID;
        sensor.type = SensorsType.SENSOR_TYPE_ACCELEROMETER;

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
            case ODR_12_5:
                sensor.min_delay = (int) (1000000 / 12.5);
                break;
            case ODR_06_25:
                sensor.min_delay = (int) (1000000 / 6.25);
                break;
            case ODR_01_56:
                sensor.min_delay = (int) (1000000 / 1.5625);
                break;
        }
        if (_operatingModeEnum == OperatingModeEnum.HybridMode)
            sensor.min_delay *= 2;
        int tempDelay = sensor.min_delay;

        switch (_range) {
            case RANGE_2G:
                sensor.max_value = (float) (2.0 * SENSORS_GRAVITY_STANDARD);
                sensor.min_value = (float) (-1.999 * SENSORS_GRAVITY_STANDARD);
                sensor.resolution = (1 / SENSITIVITY_2G) * SENSORS_GRAVITY_STANDARD;
                break;
            case RANGE_4G:
                sensor.max_value = (float) (4.0 * SENSORS_GRAVITY_STANDARD);
                sensor.min_value = (float) (-3.999 * SENSORS_GRAVITY_STANDARD);
                sensor.resolution = (1 / SENSITIVITY_4G) * SENSORS_GRAVITY_STANDARD;
                break;
            case RANGE_8G:
                sensor.max_value = (float) (8.0 * SENSORS_GRAVITY_STANDARD);
                sensor.min_value = (float) (-7.999 * SENSORS_GRAVITY_STANDARD);
                sensor.resolution = (1 / SENSITIVITY_8G) * SENSORS_GRAVITY_STANDARD;
                break;
        }
        lst.add(sensor);

        sensor = new SensorDetails();
        sensor.name = "FXOS8700CQ";
        sensor.version = 1;
        sensor.sensor_id = FXOS8700CQ_ID;
        sensor.type = SensorsType.SENSOR_TYPE_MAGNETIC_FIELD;
        sensor.min_delay = tempDelay;
        sensor.max_value = 1200;
        sensor.min_value = -1200;
        sensor.resolution = 0.1F; // TBD

        lst.add(sensor);

        return lst;
    }
}
