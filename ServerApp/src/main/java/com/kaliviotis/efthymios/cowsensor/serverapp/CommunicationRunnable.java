package com.kaliviotis.efthymios.cowsensor.serverapp;

import android.util.Log;

import com.kaliviotis.efthymios.cowsensor.commons.Constants;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Efthymios on 10/19/2017.
 */

public class CommunicationRunnable implements Runnable {
    protected Socket clientSocket = null;
//    protected String serverText   = null;

    public CommunicationRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
//        this.serverText   = serverText;
    }

    public void run() {
        try {
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String UniqueID = input.readLine();
            Log.d("CommunicationRunnable", "Receiving data from: " + UniqueID + "\n");

            ArrayList<String> lines = new ArrayList<>();

            String line;
            line = input.readLine();
            if (line != null) {
                do {
                    try {
//                        int iLinesNum = 0;
                        do {
//                            iLinesNum++;
                            lines.add(line);
                            Log.d("CommunicationRunnable_NewLine", line + "\n");
                            line = input.readLine();
                            if (line == null)
                                throw new IOException("NULL line returned");
                        } while (!line.equals(Constants.TerminatingLine));
                        Log.d("CommunicationRunnable", "Total Lines num: " + String.valueOf(lines.size()));

                        String fileName = "ClientData/Steps_" + UniqueID + ".dat";
                        // Store or append the lines to a file.
                        OutputStream outputFileStream = null;
                        try {
                            File fl = new File(Globals.getInstance().getContext().getFilesDir(), fileName);
                            outputFileStream = new BufferedOutputStream(new FileOutputStream(fl, true));
                            for(String ln : lines) {
                                ln += "\n";
                                outputFileStream.write(ln.getBytes());
                            }

                            output.write(Constants.OKLine);
                            output.newLine();
                            output.write(lines.size());
                            output.flush();

                            outputFileStream.close();
                        } catch (IOException e) {
                            output.write(Constants.ErrorLine);
                            output.newLine();

                            e.printStackTrace();
                        }

                        lines = new ArrayList<>();

                        Log.d("CommunicationRunnable", "Waiting new data...");
                        line = input.readLine();
                        if (line == null)
                            throw new IOException("NULL line returned");
                    }
                    catch (IOException e) {
                        //TODO: Possibly comment out the bellow line.
                        e.printStackTrace();
                        output.write(Constants.ErrorLine);
                        output.newLine();
                        break;
                    }
                } while (!line.equals(Constants.ConnectionTerminateLine));
                Log.d("CommunicationRunnable", "Communication finished successfully");
            }
            output.close();
            input.close();
            clientSocket.close();
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}
