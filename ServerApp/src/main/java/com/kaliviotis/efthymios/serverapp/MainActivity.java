package com.kaliviotis.efthymios.serverapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;

import com.kaliviotis.efthymios.commons.commons.nsd.NsdHelper;

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
    public static final String DEVICE_LIST_CHANGED = "device_list_updated";

    public static final String CHAT_REQUEST_RECEIVED = "chat_request_received";
    public static final String CHAT_RESPONSE_RECEIVED = "chat_response_received";
    public static final String KEY_CHAT_REQUEST = "chat_requester_or_responder";
    public static final String KEY_IS_CHAT_REQUEST_ACCEPTED = "is_chat_request_Accespter";

    NsdHelper mNsdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNsdHelper = new NsdHelper(this);
        mNsdHelper.initializeNsd();
        mNsdHelper.discoverServices();
    }

    @Override
    protected void onPause() {
        if (mNsdHelper != null) {
            mNsdHelper.stopDiscovery();
        }
        this.unregisterReceiver(localDashReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NsdHelper.BROADCAST_TAG);
        filter.addAction(DEVICE_LIST_CHANGED);
        filter.addAction(CHAT_REQUEST_RECEIVED);
        filter.addAction(CHAT_RESPONSE_RECEIVED);
        this.registerReceiver(localDashReceiver, filter);
        this.sendBroadcast(new Intent(DEVICE_LIST_CHANGED));

        mNsdHelper.registerService(12345);
//        appController.startConnectionListener();
        //mNsdHelper.registerService(ConnectionUtils.getPort(LocalDashNSD.this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mNsdHelper.tearDown();
        mNsdHelper = null;
    }

    private BroadcastReceiver localDashReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case NsdHelper.BROADCAST_TAG:
//                    NsdServiceInfo serviceInfo = intent.getParcelableExtra(NsdHelper
//                            .KEY_SERVICE_INFO);
//                    String[] serviceSplit = serviceInfo.getServiceName().split(":");
//                    String ip = serviceSplit[1];
//                    int port = Integer.parseInt(serviceSplit[2]);
//                    DataSender.sendCurrentDeviceData(LocalDashNSD.this, ip, port, true);
                    NsdServiceInfo serviceInfo = mNsdHelper.getChosenServiceInfo();
                    String ipAddress = serviceInfo.getHost().getHostAddress();
                    int port = serviceInfo.getPort();

//                    DataSender.sendCurrentDeviceData(LocalDashNSD.this, ipAddress, port, true);
                    break;
                case DEVICE_LIST_CHANGED:
/*                    ArrayList<DeviceDTO> devices = DBAdapter.getInstance(LocalDashNSD.this)
                            .getDeviceList();
                    int peerCount = (devices == null) ? 0 : devices.size();
                    if (peerCount > 0) {
                        progressBarLocalDash.setVisibility(View.GONE);
                        deviceListFragment = new PeerListFragment();
                        Bundle args = new Bundle();
                        args.putSerializable(PeerListFragment.ARG_DEVICE_LIST, devices);
                        deviceListFragment.setArguments(args);

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.deviceListHolder, deviceListFragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                        ft.commit();
                    }
                    setToolBarTitle(peerCount);*/
                    break;
                case CHAT_REQUEST_RECEIVED:
/*                    DeviceDTO chatRequesterDevice = (DeviceDTO) intent.getSerializableExtra(DataHandler
                            .KEY_CHAT_REQUEST);
                    //showChatRequestedDialog(chatRequesterDevice);
                    DialogUtils.getChatRequestDialog(LocalDashNSD.this, chatRequesterDevice).show();*/
                    break;
                case CHAT_RESPONSE_RECEIVED:
/*                    boolean isChatRequestAccepted = intent.getBooleanExtra(DataHandler
                            .KEY_IS_CHAT_REQUEST_ACCEPTED, false);
                    if (!isChatRequestAccepted) {
                        NotificationToast.showToast(LocalDashNSD.this, "Chat request " +
                                "rejected");
                    } else {
                        DeviceDTO chatDevice = (DeviceDTO) intent.getSerializableExtra(DataHandler
                                .KEY_CHAT_REQUEST);
                        DialogUtils.openChatActivity(LocalDashNSD.this, chatDevice);
                        NotificationToast.showToast(LocalDashNSD.this, chatDevice
                                .getPlayerName() + "Accepted Chat request");
                    }*/
                    break;
                default:
                    break;

            }
        }
    };

}
