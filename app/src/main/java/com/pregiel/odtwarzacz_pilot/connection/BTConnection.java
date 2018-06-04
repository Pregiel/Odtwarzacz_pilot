package com.pregiel.odtwarzacz_pilot.connection;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;

import com.pregiel.odtwarzacz_pilot.Views.PilotView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Pregiel on 08.04.2018.
 */

public class BTConnection extends Connection {


    private final static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final static int REQUEST_ENABLE_BT = 1;

    private BluetoothDevice dev;
    private BluetoothSocket socket;

    private BluetoothAdapter mBluetoothAdapter;
    private SingBroadcastReceiver mReceiver;

    public BTConnection() {

    }

    //search device
    public void searchForDevice(Activity activity) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            System.out.println("Device doesn't support Bluetooth");
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            if (mBluetoothAdapter.isEnabled()) {
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        System.out.println(deviceName);
//                        TextView connectedDevice = findViewById(R.id.connected_device);
//                        connectedDevice.setText(deviceName);
                        dev = device;
                    }
                } else {
//                    Intent discoverableIntent =
//                            new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//                    startActivity(discoverableIntent);

                    if (mBluetoothAdapter.isDiscovering()) {
                        System.out.println("juz szukalem urzadzen");
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    mBluetoothAdapter.startDiscovery();

                    mReceiver = new SingBroadcastReceiver();
                    dev = mReceiver.getDevice();
//                    TextView connectedDevice = findViewById(R.id.connected_device);
//                    connectedDevice.setText(dev.getName());
                    IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    activity.registerReceiver(mReceiver, ifilter);


                }
            }
        }
    }

    private class SingBroadcastReceiver extends BroadcastReceiver {

        private BluetoothDevice device;

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Discovery has found a device. Get the BluetoothDevice
            // object and its info from the Intent.
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address
            System.out.println(deviceHardwareAddress);
//            }
        }

        public BluetoothDevice getDevice() {
            return device;
        }
    }

    //connect


    @Override
    public void connect(PilotView view) {
//        String address = "00:15:83:07:D5:DA";
        if (mBluetoothAdapter.isEnabled()) {
            try {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(dev.getAddress());
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                System.out.println("In onResume() and socket create failed: " + e.getMessage() + ".");
            }

            mBluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
                System.out.println("\n...Connection established and data link opened...");

                setConnected(true);
                setStreams(view, socket.getInputStream(), socket.getOutputStream());
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e2) {
                    System.out.println("In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                }
            }


        }
    }


}
