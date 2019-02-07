package com.pregiel.odtwarzacz_pilot.connection;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.Utils;
import com.pregiel.odtwarzacz_pilot.connection.FoundedDevices.FoundedElement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Pregiel on 08.04.2018.
 */

public class BTConnection extends Connection {


    private final static UUID MY_UUID = UUID.fromString("b6324e17-d602-4765-822a-e68be2112efd");
    private final static int REQUEST_ENABLE_BT = 1;

    private static BluetoothSocket socket;

    private static BluetoothAdapter bluetoothAdapter;

    public BTConnection() {

    }

    public static void stopSearching() {

    }

    public static void searchForDevice() {

        getFoundedList().clear();
        Connection.setFoundedDevicesAdapter();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                MainActivity.getInstance().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            if (bluetoothAdapter.isEnabled()) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {

                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress();
                        System.out.println(deviceName);

                        if (!getFoundedList().addressInList(deviceHardwareAddress)) {
                            getFoundedList().add(deviceHardwareAddress, deviceName);
                            Connection.setFoundedDevicesAdapter();
                        }
                    }
                }
            }
        } else {
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                    builder.setMessage(R.string.no_bluetooth_support)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
    }

    private static boolean connecting;

    public static void connect(FoundedElement element) {
        if (!connecting)
            new LongOperationConnect(element).execute();
    }

    private static class LongOperationConnect extends AsyncTask<Void, Void, Void> {
        FoundedElement element;

        public LongOperationConnect(FoundedElement element) {
            this.element = element;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            connecting = true;
            if (bluetoothAdapter.isEnabled()) {
                try {
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(element.getAddress());
                    socket = device.createRfcommSocketToServiceRecord(MY_UUID);

                    socket.connect();
                    System.out.println("Connected with " + element.getName());

                    DataInputStream DIS = new DataInputStream(
                            socket.getInputStream());
                    DataOutputStream DOS = new DataOutputStream(
                            socket.getOutputStream());


                    setConnected(true);
                    setStreams(DIS, DOS);
                } catch (IOException e) {
                    e.printStackTrace();
                    connecting = false;
                    try {
                        socket.close();
                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                                builder.setMessage(MainActivity.getInstance().getString(R.string.connecting_error, element.getName()))
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        connecting = false;
                    }
                }
            }
            connecting = false;
            return null;
        }
    }

}
