package com.pregiel.odtwarzacz_pilot.connection;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;

import org.xbill.DNS.Address;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Pregiel on 08.04.2018.
 */

public class WifiConnection extends Connection {
    private static Socket socket;

    private final static int PORT = 1755;
    public final static int TIMEOUT_REACHABLE = 200;
    public final static int TIMEOUT_CONNECTING = 3500;


    public WifiConnection() {
        socket = null;
    }

    //main functions


    public static void searchDevices() {
        new LongOperationSearch().execute();
    }

    public void disconnect() {
        new LongOperationDisconnect().execute();
    }

    //connection

    private static boolean stopSearching;

    public static void stopSearching() {
        stopSearching = true;
    }

    private static List<String> founded;

    public static List<String> getFoundedList() {
        return founded;
    }

    public static void initFoundedList() {
        founded = new ArrayList<>();
    }

    public static void setFoundedList(List<String> founded) {
        WifiConnection.founded = founded;
    }

    private static class LongOperationSearch extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            String ip = getWifiApIpAddress();
            stopSearching = false;

            System.out.println("My ip: " + ip);

            String subnet = ip.substring(0, ip.lastIndexOf(".") + 1);

            selectedHost = null;
            while (!stopSearching) {
                for (int i = 1; i < 255; i++) {
                    String host = subnet + i;
                    if (isReachableByTcp(host)) {
                        if (!founded.contains(host)) {
                            founded.add(host);
                            Connection.setFoundedDevicesAdapter();
                        }
                        System.out.println("founded: " + host);
                        break;
                    } else {
                        if (founded.contains(host)) {
                            founded.remove(host);
                            Connection.setFoundedDevicesAdapter();
                        }
                    }
                    if (stopSearching || selectedHost != null) {
                        break;
                    }
                }
            }
//            if (selectedHost != null) {
//                try {
//                    socket = new Socket(selectedHost, 1755);
//                    setConnected(true);
//                    setStreams(socket.getInputStream(), socket.getOutputStream());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            return null;
        }
    }

    private static String selectedHost;

    public static void connect(String host) {
        connect(host, null);
    }

    public static void connect(String host, Context context) {
        stopSearching = true;
        selectedHost = host;
        new LongOperationConnect(context).execute();
    }

    public static void checkAndConnect(String host, Context context) {
        selectedHost = host;
        new LongOperationConnect(context, true).execute();

    }

    private static class LongOperationConnect extends AsyncTask<Void, Void, Void> {
        private Context context;
        private boolean check;

        public LongOperationConnect(Context context) {
            this.context = context;
        }

        public LongOperationConnect(Context context, boolean check) {
            this.context = context;
            this.check = check;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Toast[] toast = new Toast[1];
            if (context != null) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast[0] = Toast.makeText(context, context.getString(R.string.connecting_with, selectedHost), Toast.LENGTH_LONG);
                        toast[0].show();
                    }
                });
            }
            try {
                if (check) {
                    isReachableByTcp(selectedHost, TIMEOUT_CONNECTING);
                }

                socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress(selectedHost, PORT);
                socket.connect(socketAddress, TIMEOUT_CONNECTING);

                setConnected(true);
                setStreams(socket.getInputStream(), socket.getOutputStream());


                SharedPreferences recentPref = MainActivity.getInstance().getBaseContext().getSharedPreferences(RECENT_CONNECTION_TAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor recentPrefEditor = recentPref.edit();

                String first = recentPref.getString("first", "none");
                if (!first.equals("none")) {
                    recentPrefEditor.putString("second", first);
                    recentPrefEditor.putString("second_name", first);
                }

                recentPrefEditor.putString("first", selectedHost);

                recentPrefEditor.apply();

            } catch (SocketTimeoutException ex) {
                if (context != null) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TODO: splash screen
                            Toast.makeText(context, context.getString(R.string.connecting_error, selectedHost), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            toast[0].cancel();
            return null;
        }
    }

    public static boolean isReachableByTcp(String host) {
        return isReachableByTcp(host, TIMEOUT_REACHABLE);
    }

    public static boolean isReachableByTcp(String host, int timeout) {
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(host, PORT);
            socket.connect(socketAddress, timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static String getWifiApIpAddress() {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    // disconnect

    private class LongOperationDisconnect extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
