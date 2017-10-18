package com.kaliviotis.efthymios.cowsensor;

/**
 * Created by Efthymios on 10/17/2017.
 */

public class StepDetector {
    int iHalfStepsCount;
    int iAveragingInterval;

    private class AccellValues {
        public double dMagnitude;
        public int iMSecDiff;
    }

    AccellValues[] lastAccellValues;

    int iEndAccellValPos;
    double maxAccellMagnitude;
    double minAccellMagnitude;

    long lLastAccellValueOffset;

    double dAccelSumVals;
    double dAccelMSecVals;

    public StepDetector() {
        iHalfStepsCount = 0;
        iAveragingInterval = 200;

        iEndAccellValPos = -1;
        lastAccellValues = new AccellValues[50];
        for (int i = 0; i < lastAccellValues.length; ++i)
        {
            AccellValues vl = new AccellValues();
            vl.dMagnitude = 1;
            vl.iMSecDiff = 100;
            lastAccellValues[i] = vl;
        }
        maxAccellMagnitude = 0;
        minAccellMagnitude = 100;

        dAccelSumVals = 4000.0;
        dAccelMSecVals = 4000.0;
    }

    public void NewAccelerometerReading(long ts, float X, float Y, float Z) {
        double curMagn = Math.sqrt(X * X + Y * Y + Z * Z);

        if (iEndAccellValPos == -1)
        {
            iEndAccellValPos = 0;
            lastAccellValues[0].dMagnitude = curMagn;
            lLastAccellValueOffset = ts;
            return;
        }

        lastAccellValues[iEndAccellValPos].iMSecDiff = (int) (ts - lLastAccellValueOffset);

        // We calculate the total average g's over a period of 20 seconds.
        dAccelSumVals += lastAccellValues[iEndAccellValPos].dMagnitude * lastAccellValues[iEndAccellValPos].iMSecDiff;
        dAccelMSecVals += lastAccellValues[iEndAccellValPos].iMSecDiff;
        double dTrueAvg;
        try { dTrueAvg = dAccelSumVals / dAccelMSecVals; }
        catch (Exception e) { dTrueAvg = 1.0; }

        if (dAccelMSecVals > 20000.0)
        {
            dAccelMSecVals = 4000;
            dAccelSumVals = dAccelMSecVals * dTrueAvg;
        }

        // Calculating moving average of the last iAveragingInterval msec (eg 200msec).
        boolean bFound = false;
        int iTotalMSecs = 0;
        double dMovingAvgMagnitude = 0;
        for (int i = iEndAccellValPos; i >= 0; --i)
        {
            if ((iTotalMSecs + lastAccellValues[i].iMSecDiff) > iAveragingInterval)
            {
                dMovingAvgMagnitude += (iAveragingInterval - iTotalMSecs) * lastAccellValues[i].dMagnitude;
                bFound = true;
                break;
            }
            else
            {
                dMovingAvgMagnitude += lastAccellValues[i].iMSecDiff * lastAccellValues[i].dMagnitude;
            }
            iTotalMSecs += lastAccellValues[i].iMSecDiff;
        }
        if (!bFound)
        {
            for (int i = lastAccellValues.length - 1; i >= 0; --i)
            {
                if ((iTotalMSecs + lastAccellValues[i].iMSecDiff) > iAveragingInterval)
                {
                    dMovingAvgMagnitude += (iAveragingInterval - iTotalMSecs) * lastAccellValues[i].dMagnitude;
                    bFound = true;
                    break;
                }
                else
                {
                    dMovingAvgMagnitude += lastAccellValues[i].iMSecDiff * lastAccellValues[i].dMagnitude;
                }
                iTotalMSecs += lastAccellValues[i].iMSecDiff;
            }
        }
        dMovingAvgMagnitude /= iAveragingInterval;

        iEndAccellValPos++;
        if (iEndAccellValPos == lastAccellValues.length)
            iEndAccellValPos = 0;
        lastAccellValues[iEndAccellValPos].dMagnitude = curMagn;
        lLastAccellValueOffset = ts;


        boolean bResetLimits = false;

        if (dMovingAvgMagnitude > maxAccellMagnitude) maxAccellMagnitude = dMovingAvgMagnitude;
        if (dMovingAvgMagnitude < minAccellMagnitude) minAccellMagnitude = dMovingAvgMagnitude;

        //Debug.WriteLine("Max: " + maxAccellMagnitude + " Min: " + minAccellMagnitude);
        //if ((maxAccellMagnitude > 1.15) && (minAccellMagnitude < 0.85))
        if ((maxAccellMagnitude > (dTrueAvg + 0.10)) && (minAccellMagnitude < (dTrueAvg - 0.10)))
        {
            iHalfStepsCount++;
            bResetLimits = true;
        }

        if (bResetLimits)
        {
            maxAccellMagnitude = 0;
            minAccellMagnitude = 100;
        }
    }

    public int GetCompletedSteps() {
        return (iHalfStepsCount / 2);
    }

    public void ResetSteps() {
        iHalfStepsCount = 0;
    }
}
