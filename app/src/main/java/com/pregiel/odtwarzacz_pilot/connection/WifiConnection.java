package com.pregiel.odtwarzacz_pilot.connection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.Utils;
import com.pregiel.odtwarzacz_pilot.connection.FoundedDevices.FoundedElement;
import com.pregiel.odtwarzacz_pilot.connection.RecentConnected.RecentElement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
    private final static String SEARCHING = "SEARCHING";
    private final static String CONNECTING = "CONNECTING";

    public final static int TIMEOUT_REACHABLE = 200;
    public final static int TIMEOUT_CONNECTING = 3500;


    public WifiConnection() {
        socket = null;
    }

    //main functions


    public static void searchDevices() {
        new LongOperationSearch().execute();
    }

    //connection

    private static boolean stopSearching;

    public static void stopSearching() {
        stopSearching = true;
    }


    private static class LongOperationSearch extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            WifiManager wifiManager = (WifiManager) MainActivity.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            int ipInt = wifiManager.getConnectionInfo().getIpAddress();
            int netMask = wifiManager.getDhcpInfo().netmask;

            int ones = Integer.toBinaryString(netMask).length();
            int zeros = 32 - ones;
            int maxHosts = (int) Math.pow(2, zeros);

            int ipAddressSpace = Integer.reverseBytes(ipInt & netMask);

            stopSearching = false;
            selectedHost = null;

            while (!stopSearching && selectedHost == null) {
                for (int i = 0; i < maxHosts; i++) {
                    String host = Utils.intToIp(ipAddressSpace + i);
                    String hostName = isReachableByTcp(host);

                    if (i == 2) {
                        System.out.println(host);
                    }
                    if (hostName != null) {
                        if (!getFoundedList().addressInList(host)) {
                            getFoundedList().add(host, hostName);
                            Connection.setFoundedDevicesAdapter();
                        }
                    } else {
                        getFoundedList().removeIfInList(host);
                        Connection.setFoundedDevicesAdapter();
                    }

                    if (stopSearching || selectedHost != null) {
                        break;
                    }
                }
            }
            getFoundedList().clear();
            return null;
        }
    }


    private static String selectedHost;

    public static void connect(String host) {
        connect(host, MainActivity.getInstance().getApplicationContext());
    }

    public static void connect(String host, Context context) {
        stopSearching = true;
        selectedHost = host;
        new LongOperationConnect(context).execute();
    }

    public static void checkAndConnect(String host, Context context, Runnable errorRunnable) {
        selectedHost = host;
        new LongOperationConnect(context, true, errorRunnable).execute();

    }

    private static class LongOperationConnect extends AsyncTask<Void, Void, Void> {
        private Context context;
        private boolean check;
        private Toast toast;
        private Runnable endRunnable;

        public LongOperationConnect(@NonNull Context context) {
            this.context = context;
        }

        public LongOperationConnect(@NonNull Context context, boolean check) {
            this(context);
            this.check = check;
        }

        public LongOperationConnect(@NonNull Context context, boolean check, Runnable endRunnable) {
            this(context, check);
            this.endRunnable = endRunnable;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toast = Toast.makeText(context, context.getString(R.string.connecting_with, selectedHost), Toast.LENGTH_LONG);
                    toast.show();
                }
            });

            try {
                if (check) {
                    isReachableByTcp(selectedHost, TIMEOUT_CONNECTING);
                }

                socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress(selectedHost, PORT);
                socket.connect(socketAddress, TIMEOUT_CONNECTING);


                DataInputStream DIS = new DataInputStream(socket.getInputStream());
                DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                DOS.writeUTF(CONNECTING);

                setConnected(true);
                setStreams(DIS, DOS);


                SharedPreferences recentPref = MainActivity.getInstance().getBaseContext().getSharedPreferences(RECENT_CONNECTION_TAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor recentPrefEditor = recentPref.edit();

                String first = recentPref.getString("first", "none");
                if (!first.equals("none")) {
                    if (!first.equals(selectedHost)) {
                        String first_name = recentPref.getString("first_name", "none");
                        recentPrefEditor.putString("second", first);
                        recentPrefEditor.putString("second_name", first_name);
                    }
                }

                recentPrefEditor.putString("first", selectedHost);

                recentPrefEditor.apply();

            } catch (SocketTimeoutException ex) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO: splash screen
                        if (toast != null) {
                            toast.cancel();
                        }
                        Toast.makeText(context, context.getString(R.string.connecting_error, selectedHost), Toast.LENGTH_SHORT).show();

                        if (endRunnable != null) {
                            endRunnable.run();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (toast != null) {
                toast.cancel();
            }
            if (endRunnable != null) {
                endRunnable.run();
            }
            return null;
        }
    }

    public static String isReachableByTcp(String host) {
        return isReachableByTcp(host, TIMEOUT_REACHABLE);
    }

    public static String isReachableByTcp(String host, int timeout) {
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(host, PORT);
            socket.connect(socketAddress, timeout);

            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
            DataInputStream DIS = new DataInputStream(socket.getInputStream());

            DOS.writeUTF(SEARCHING);
            String msg = DIS.readUTF();

            DIS.close();
            DOS.close();
            socket.close();
            return msg;
        } catch (IOException e) {
            return null;
        }
    }

    public static void checkAndDo(Runnable runnable) {
        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.getInstance().getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo.isConnected()) {
            runnable.run();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
            builder.setMessage(R.string.connecting_wifi_error)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

}
