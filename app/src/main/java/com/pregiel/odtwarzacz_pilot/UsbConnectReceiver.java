package com.pregiel.odtwarzacz_pilot;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.pregiel.odtwarzacz_pilot.connection.UsbConnection;

//adb shell am broadcast -a com.pregiel.odtwarzacz_pilot.UsbConnectReceiver.ACTION_USB_CONNECT
public class UsbConnectReceiver extends BroadcastReceiver {
    public static final String ACTION_USB_CONNECT = "com.pregiel.odtwarzacz_pilot.UsbConnectReceiver.ACTION_USB_CONNECT";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ContextCompat.checkSelfPermission(MainActivity.getInstance().getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.getInstance().getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            UsbConnection.connect();
        }
    }
}
