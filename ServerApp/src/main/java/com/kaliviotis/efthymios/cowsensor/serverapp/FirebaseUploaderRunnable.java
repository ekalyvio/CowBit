package com.kaliviotis.efthymios.cowsensor.serverapp;

import android.util.Log;

import com.kaliviotis.efthymios.cowsensor.commons.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Efthymios on 10/24/2017.
 */

public class FirebaseUploaderRunnable implements Runnable {
    private static final String userEmail = "test@test.com";
    private static final String userPassword = "123456";

    FirebaseHelper firebaseHelper;
    Map<String, Object> childUpdates;
    ArrayList<String> triggerValues;
    String uniqueID;

    @Override
    public void run() {
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.Init();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (!firebaseHelper.IsUserLogedIn()) {
                    firebaseHelper.signIn(userEmail, userPassword);
                } else {
                    if (firebaseHelper.getClientID() == null) {
                        firebaseHelper.readClientID();
                    } else {
                        ProcessFiles();
                    }
                }
//                Thread.sleep(15000);
                Thread.sleep(5 * 60 * 1000);
            }
        } catch (InterruptedException ex) {
            //Thread.currentThread().interrupt(); // very important
        }

        //firebaseHelper.
    }

    private void ProcessFiles() {
        String clientID = firebaseHelper.getClientID();

        File directory = new File(Globals.getInstance().getContext().getFilesDir().toString() + File.separator + "ClientData");
        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith("steps_");
            }
        });

        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
            uniqueID = files[i].getName().substring(6, files[i].getName().length() - 4);

            DatesTree datesTree = new DatesTree();

            try (BufferedReader br = new BufferedReader(new FileReader(files[i]))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals(""))
                        break;
                    String year = line.substring(0, 4);
                    String month = line.substring(4, 6);
                    String day = line.substring(6, 8);
                    String hour = line.substring(8, 10);
                    String minute = line.substring(10, 12);

                    String steps = line.substring(13);

                    datesTree.AddSteps(
                            Integer.valueOf(year),
                            Integer.valueOf(month),
                            Integer.valueOf(day),
                            Integer.valueOf(hour),
                            Integer.valueOf(minute),
                            Integer.valueOf(steps)
                    );
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            childUpdates = new HashMap<>();

            triggerValues = new ArrayList<>();

            String clientCow = String.format("/clients/%s/cows/%s/last_seen", firebaseHelper.getClientID(), uniqueID);

            childUpdates.put(clientCow, System.currentTimeMillis());

//            lastHourProcessed = -1;
            datesTree.IterateItems(new DatesTree.CallbackInterface() {
                @Override
                public void IteratorCallback(int year, int month, int day, int hour, int minute, int stepsNum) {
                    String path = String.format("/cow_data/%s/%d/%d/%d/%d/%d/", uniqueID, year, month, day, hour, minute);
                    childUpdates.put(path, stepsNum);

                    String hourVal = String.format("%02d%02d%02d%02d", year, month, day, hour);

                    if (!triggerValues.contains(hourVal)) {
                        triggerValues.add(hourVal);
                        String hourPath = String.format("/trig_process/%s/%s", uniqueID, hourVal);
                        childUpdates.put(hourPath, "");
                    }
                }
            });

            try {
                firebaseHelper.updateChildren(childUpdates);
                //TODO: Uncomment the bellow line
                files[i].delete();
            } catch (Exception e) {
            }
        }

    }
}
