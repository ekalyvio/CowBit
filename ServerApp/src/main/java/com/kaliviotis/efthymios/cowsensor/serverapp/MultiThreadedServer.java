package com.kaliviotis.efthymios.cowsensor.serverapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Efthymios on 10/19/2017.
 */

public class MultiThreadedServer implements Runnable {
    int serverPort;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped;

    public MultiThreadedServer(){
        isStopped = true;
    }

    public void run(){
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            new Thread(
                    new CommunicationRunnable(clientSocket)
            ).start();
        }
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private synchronized void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(0);
            setLocalPort(serverSocket.getLocalPort());
            isStopped = false;
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }

    public synchronized int getLocalPort() {
        return serverPort;
    }

    public void setLocalPort(int port) {
        serverPort = port;
    }
}
