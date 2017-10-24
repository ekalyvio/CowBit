package com.kaliviotis.efthymios.cowsensor.commons.nsd;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

public class NsdHelper {
    public static final int DISCOVERY_STARTED_ID = 1;

/*    public static final String BROADCAST_TAG = "NSDBroadcast";
    public static final String KEY_SERVICE_INFO = "serviceinfo";*/

    Context mContext;

//    private LocalBroadcastManager broadcaster;

    NsdManager mNsdManager;
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;

    //public static final String SERVICE_TYPE = "_http._tcp";
    public static final String SERVICE_TYPE = "_aahttp._tcp";

    // There is an additional dot at the end of service name most probably by os, this is to
    // rectify that problem
//    public static final String SERVICE_TYPE_PLUS_DOT = SERVICE_TYPE + ".";

    public static final String TAG = "NsdHelper: ";

//    private static final String DEFAULT_SERVICE_NAME = "LocalDashKK";
    public String mServiceName;

    public enum CallbackTypeEnum {
        DiscoveryStarted,
        ServiceFound,
        ServiceLost,
        DiscoveryStopped,
        StartDiscoveryFailed,
        StopDiscoveryFailed,
        ResolveFailed,
        ServiceResolved,
        ServiceRegistered,
        RegistrationFailed,
        ServiceUnregistered,
        UnregistrationFailed
    }

    public interface CallbackInterface{
        void doCallback(CallbackTypeEnum type);
    }

    NsdServiceInfo mService;
    CallbackInterface callback;

    ArrayList<NsdServiceInfo> servicesFound;

    public NsdHelper(Context context, String serviceName, CallbackInterface callback) {
        mContext = context;
        mServiceName = serviceName;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);

        this.callback = callback;
//        broadcaster = LocalBroadcastManager.getInstance(mContext);
    }

/*    public void initializeNsd() {
        initializeResolveListener();
        //mNsdManager.init(mContext.getMainLooper(), this);
    }*/

    private void callCallback(CallbackTypeEnum type) {
        if (callback != null)
            callback.doCallback(type);
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
                callCallback(CallbackTypeEnum.DiscoveryStarted);
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success" + service);
                Log.d(TAG, "Service discovery success: " + service.getServiceName());

                // For some reason the service type received has an extra dot with it, hence
                // handling that case
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Possibly Same machine: " + mServiceName);
                } else if (service.getServiceName().contains(mServiceName)) {
                    Log.d(TAG, "Possibly different machines. (" + service.getServiceName() + "-" +
                            mServiceName + ")");
                }
                servicesFound.add(service);
                callCallback(CallbackTypeEnum.ServiceFound);

/*                if (bResolve)
                    mNsdManager.resolveService(service, mResolveListener);*/
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                if (mService == service) {
                    mService = null;
                }
                callCallback(CallbackTypeEnum.ServiceLost);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
                callCallback(CallbackTypeEnum.DiscoveryStopped);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
                callCallback(CallbackTypeEnum.StartDiscoveryFailed);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
                callCallback(CallbackTypeEnum.StopDiscoveryFailed);
            }
        };
    }

    public NsdServiceInfo getLastServiceFound() {
        if (servicesFound.size() == 0)
            return null;
        return servicesFound.get(servicesFound.size() - 1);
    }

    public ArrayList<NsdServiceInfo> getAllServicesFound() {
        return servicesFound;
    }

    public void ResolveService(NsdServiceInfo service) {
        mNsdManager.resolveService(service, mResolveListener);
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed " + errorCode);
                mService = null;
                callCallback(CallbackTypeEnum.ResolveFailed);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.v(TAG, "Resolve Succeeded. " + serviceInfo);
                if (serviceInfo.getServiceName().equals(mServiceName)) {
//                    Log.d(TAG, "Same IP.");
//                    return;
                }
                mService = serviceInfo;
                callCallback(CallbackTypeEnum.ServiceResolved);
            }
        };
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                mServiceName = NsdServiceInfo.getServiceName();
                Log.d(TAG, "Service registered: " + NsdServiceInfo);
                callCallback(CallbackTypeEnum.ServiceRegistered);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
                Log.d(TAG, "Service registration failed: " + arg1);
                callCallback(CallbackTypeEnum.RegistrationFailed);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                Log.d(TAG, "Service unregistered: " + arg0.getServiceName());
                callCallback(CallbackTypeEnum.ServiceUnregistered);
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "Service unregistration failed: " + errorCode);
                callCallback(CallbackTypeEnum.UnregistrationFailed);
            }
        };
    }

    public void registerService(int port) {
        tearDown();  // Cancel any previous registration request
        initializeRegistrationListener();
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);
        Log.v(TAG, Build.MANUFACTURER + " registering service: " + port);
        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void discoverServices() {
        stopDiscovery();  // Cancel any existing discovery request
        mService = null;
        servicesFound = new ArrayList<>();
        initializeResolveListener();
        initializeDiscoveryListener();
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void stopDiscovery() {
        if (mDiscoveryListener != null) {
            try {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            } finally {
            }
            mDiscoveryListener = null;
        }
    }

    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }

    public void tearDown() {
        if (mRegistrationListener != null) {
            try {
                mNsdManager.unregisterService(mRegistrationListener);
            } finally {
            }
            mRegistrationListener = null;
        }
    }
}