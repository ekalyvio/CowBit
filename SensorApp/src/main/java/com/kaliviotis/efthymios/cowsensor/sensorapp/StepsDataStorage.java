package com.kaliviotis.efthymios.cowsensor.sensorapp;

import android.net.nsd.NsdServiceInfo;
import android.provider.Settings;
import android.util.Log;

import com.kaliviotis.efthymios.cowsensor.commons.Constants;
import com.kaliviotis.efthymios.cowsensor.commons.nsd.NsdHelper;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Efthymios on 10/19/2017.
 */

public class StepsDataStorage implements Runnable {
    private static int iLastStoredHalfSteps = 0;
    private static long lLastDateStored = 0;
    public int stepsCount;
    public long dateTime;

    private static long lLastUpload;
    private static final Object lock = new Object();

    public StepsDataStorage(int stepsStore, long dt) {
        stepsCount = stepsStore;
        dateTime = dt;
    }

    public static void RegisterSteps(int iHalfStepsCount) {
        long curDate = new Date().getTime();
        if ((curDate - lLastDateStored) > 60000) {
            lLastDateStored = (curDate / 60000) * 60000;
            StepsDataStorage ds = new StepsDataStorage(iHalfStepsCount, lLastDateStored);
            Thread storeThread = new Thread(ds);
            storeThread.start();
        }
    }

    private void DoStore() {
        synchronized (lock) {
            Log.d("RegisterSteps", "Entered");
            Date prevDate = new Date(dateTime);

            DateFormat dateFileFormat = new SimpleDateFormat("yyyyMMdd_HH");

            DateFormat dateLineFormat = new SimpleDateFormat("yyyyMMddHHmm");
            String fileName = "Steps/Steps_" + dateFileFormat.format(prevDate) + ".dat";

            int steps = stepsCount - iLastStoredHalfSteps;
            String line = dateLineFormat.format(prevDate) + "," + String.valueOf(steps) + "\n";


            OutputStream output = null;
            try {
                File fl = new File(Globals.getInstance().getContext().getFilesDir(), fileName);
                output = new BufferedOutputStream(new FileOutputStream(fl, true));
                output.write(line.getBytes());

//                lLastDateStored = dt;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    output.close();
                    iLastStoredHalfSteps = stepsCount;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("RegisterSteps", "Exited");
        }
    }

    private static NsdHelper mNsdHelper = null;
    private static boolean bResolvingService;

    private int SendFileContents(BufferedWriter out, File file) throws IOException {
        int lineNum = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(""))
                    break;
                lineNum++;
                out.write(line);
                out.newLine();
            }
        }
        return lineNum;
    }

    private void ServiceResolved() {
        Log.d("StepsDataStorage", "something got resolved");
        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
        if (service != null) {
            int port = service.getPort();
            InetAddress address = service.getHost();
            try {
                String android_id = Settings.Secure.getString(Globals.getInstance().getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                File directory = new File(Globals.getInstance().getContext().getFilesDir().toString() + File.separator + "Steps");
                File[] files = directory.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().startsWith("steps_");
                    }
                });
                Arrays.sort(files);
                Log.d("Files", "Size: "+ files.length);

                //  1) Get the unique ID of the device.
                //  2) Search directory for files other than the last one and get them on a list (sorted ascending).
                //  3) If the list is empty then close the socket and terminate the function.
                //  4) Send the unique ID of the device.
                //  5) For each item in the list:
                //       i) Send all the lines of that file
                //       ii) Send a terminating asterisk.
                //       iii) Wait confirmation of receival along with the line count received.
                //       iv) If it got received and line count is exactly the same, delete the file.

                if (files.length <= 0)
                    return;

                Socket socket = new Socket(address, port);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.write(android_id);
                out.newLine();
                for (int i = 0; i < files.length - 1; i++)
                {
                    Log.d("Files", "FileName:" + files[i].getName());
                    int linesNumber = SendFileContents(out, files[i]);
                    out.write(Constants.TerminatingLine);
                    out.newLine();
                    out.flush();
                    String ln = in.readLine();
                    if (ln.equals(Constants.OKLine)) {
                        int iLinesNum = in.read();
                        if (iLinesNum == linesNumber) {
                            //TODO: Uncomment the bellow line
//                            files[i].delete();
                        }
                    }
                    if (ln.equals(Constants.ErrorLine)) {
                        Log.d("StepsDataStorage", "Error sending file");
                    }
                }
                out.write(Constants.ConnectionTerminateLine);
                out.newLine();

                out.close();
                in.close();

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mNsdHelper.stopDiscovery();
        mNsdHelper.tearDown();
    }

    private void DoUpload() {
        if (mNsdHelper == null)
            mNsdHelper = new NsdHelper(Globals.getInstance().getContext(), Constants.NsdName, new NsdHelper.CallbackInterface() {
                @Override
                public void doCallback(NsdHelper.CallbackTypeEnum type) {
                    switch (type) {
                        case DiscoveryStarted:
                            bResolvingService = false;
                            break;
                        case ServiceFound:
                            if (bResolvingService)
                                return;
                            bResolvingService = true;
                            NsdServiceInfo service = mNsdHelper.getLastServiceFound();
                            if (service != null)
                                mNsdHelper.ResolveService(service);
                            break;
                        case ServiceLost:
                            break;
                        case DiscoveryStopped:
                            break;
                        case StartDiscoveryFailed:
                            break;
                        case StopDiscoveryFailed:
                            break;
                        case ResolveFailed:
                            break;
                        case ServiceResolved:
                            ServiceResolved();
                            break;
                        case ServiceRegistered:
                            break;
                        case RegistrationFailed:
                            break;
                        case ServiceUnregistered:
                            break;
                        case UnregistrationFailed:
                            break;
                    }
                }
            });
//        mNsdHelper.registerService(98752);
        mNsdHelper.discoverServices();
    }

    @Override
    public void run() {
        long lMinutesToUpload = 1 * 60 * 1000;
        DoStore();
        long curDate = new Date().getTime();
        if ((curDate - lLastUpload) > lMinutesToUpload) {
            lLastUpload = curDate;
            DoUpload();
        }
    }
}
