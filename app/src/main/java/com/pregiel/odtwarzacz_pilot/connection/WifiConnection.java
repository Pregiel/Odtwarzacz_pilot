package com.pregiel.odtwarzacz_pilot.connection;

import android.os.AsyncTask;

import com.pregiel.odtwarzacz_pilot.Views.PilotView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Pregiel on 08.04.2018.
 */

public class WifiConnection extends Connection {
    private Socket socket;

    private final static int PORT = 1755;
    private final static int TIMEOUT = 200;

    private PilotView view;


    public WifiConnection() {
        this.socket = null;
    }

    //main functions



    @Override
    public void connect(PilotView view) {
        this.view = view;
        new LongOperationConnect().execute();
    }

    public void disconnect() {
            new LongOperationDisconnect().execute();
    }

    //connection

    private class LongOperationConnect extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            String ip = getWifiApIpAddress();

            System.out.println("My ip: " + ip);

            String subnet = ip.substring(0, ip.lastIndexOf(".") + 1);

            for (int i = 1; i < 255; i++) {
                String host = subnet + i;
                if (isReachableByTcp(host)) {

                    try {
                        socket = new Socket(host, 1755);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            setConnected(true);
            try {
                setStreams(view, socket.getInputStream(), socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
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

    private String getWifiApIpAddress() {

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
