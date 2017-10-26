package com.kaliviotis.efthymios.cowsensor.serverapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.kaliviotis.efthymios.cowsensor.commons.nsd.NsdHelper;
import com.kaliviotis.efthymios.cowsensor.commons.Constants;

import java.io.File;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    NsdHelper mNsdHelper;
    MultiThreadedServer server;
    int localPort;

    NsdHelper.CallbackTypeEnum RegistrationState;

    private Handler mServiceCheckHandler = new Handler();

    private Thread uploaderThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Globals.getInstance().setContext(getApplicationContext());

        File directory = new File(getFilesDir() + File.separator + "ClientData");
        directory.mkdirs();
    }

    @Override
    protected void onPause() {
/*        if (mNsdHelper != null) {
            mNsdHelper.stopDiscovery();
        }*/

        server.stop();
        mNsdHelper.tearDown();
        mNsdHelper = null;

        uploaderThread.interrupt();

        Log.d("MainActivity", "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume");

        server = new MultiThreadedServer();
        new Thread(server).start();
        while (server.isStopped) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        localPort = server.getLocalPort();

        mNsdHelper = new NsdHelper(this, Constants.NsdName, registrationCallback);
        // Server is not going to discover other services. It will only get discovered.
//        mNsdHelper.discoverServices();

        if (mNsdHelper != null) {
            RegistrationState = null;
            mNsdHelper.registerService(localPort);
            mServiceCheckHandler.postDelayed(mServiceRegisteredRunnable, 10000);
        }

        uploaderThread = new Thread(new FirebaseUploaderRunnable());
        uploaderThread.start();
    }

    NsdHelper.CallbackInterface registrationCallback = new NsdHelper.CallbackInterface() {
        @Override
        public void doCallback(NsdHelper.CallbackTypeEnum type) {
            RegistrationState = type;
            switch (type) {
                case ServiceRegistered:
                    Log.d("MainActivity", "ServiceRegistered");
                    break;
                case RegistrationFailed:
                    Log.d("MainActivity", "RegistrationFailed");
                    break;
                case ServiceUnregistered:
                    Log.d("MainActivity", "ServiceUnregistered");
                    break;
                case UnregistrationFailed:
                    Log.d("MainActivity", "UnregistrationFailed");
                    break;
            }
        }
    };

    private Runnable mServiceRegisteredRunnable = new Runnable() {
        @Override
        public void run() {
            if ((RegistrationState == null) || (RegistrationState != NsdHelper.CallbackTypeEnum.ServiceRegistered)) {
                mNsdHelper.tearDown();

                mNsdHelper = new NsdHelper(Globals.getInstance().getContext(), Constants.NsdName, registrationCallback);
                mNsdHelper.registerService(localPort);
            }
            mServiceCheckHandler.postDelayed(mServiceRegisteredRunnable, 10000);
        }
    };

    @Override
    protected void onDestroy() {
        if (mNsdHelper != null){
            mNsdHelper.tearDown();
            mNsdHelper = null;
        }
        Log.d("MainActivity", "onDestroy");
        super.onDestroy();
    }
}
