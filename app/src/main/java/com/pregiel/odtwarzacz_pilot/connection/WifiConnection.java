package com.pregiel.odtwarzacz_pilot.connection;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Pregiel on 08.04.2018.
 */

public class WifiConnection extends Connection {
    private static Socket socket;

    private final static int PORT = 1755;
    private final static int TIMEOUT = 200;


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

    public static void setFoundedList(List<String> founded) {
        WifiConnection.founded = founded;
    }

    private static class LongOperationSearch extends AsyncTask<Void, Void, Void> {


        public LongOperationSearch() {
            founded = new ArrayList<>();
        }

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
                            Connection.setFoundedDevicesAdapter(founded);
                        }
                        System.out.println("founded: " + host);

//                    try {
//                        socket = new Socket(host, 1755);
//                        stopSearching = true;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                        break;
                    } else {
                        if (founded.contains(host)) {
                            founded.remove(host);
                            Connection.setFoundedDevicesAdapter(founded);
                        }
                    }
                    if (stopSearching) {
                        break;
                    }
                }
            }
            if (selectedHost != null) {
                try {
                    socket = new Socket(selectedHost, 1755);
                    setConnected(true);
                    setStreams(socket.getInputStream(), socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private static String selectedHost;

    public static void connect(String host) {
        stopSearching = true;
        selectedHost = host;
//        new LongOperationConnect(host).execute();
    }

    private static class LongOperationConnect extends AsyncTask<Void, Void, Void> {
        private String host;

        public LongOperationConnect(String host) {
            this.host = host;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isReachableByTcp(host)) {
                try {
                    socket = new Socket(host, 1755);
                    setConnected(true);
                    setStreams(socket.getInputStream(), socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private static boolean isReachableByTcp(String host) {
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(host, PORT);
            socket.connect(socketAddress, TIMEOUT);
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
