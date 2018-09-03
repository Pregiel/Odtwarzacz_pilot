package com.pregiel.odtwarzacz_pilot.connection;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Pregiel on 08.04.2018.
 */

public class UsbConnection extends Connection {
    private static Socket socket;

    private final static int PORT = 1755;
    private final static int TIMEOUT = 200;



    public UsbConnection() {
        socket = null;
    }

    //main functions


    public static void connect() {
        new LongOperationConnect().execute();
    }

    public static void disconnect() {
            new LongOperationDisconnect().execute();
    }

    //connection

    private static class LongOperationConnect extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                setServerSocket(new ServerSocket(38300));
                socket = getServerSocket().accept();
            } catch (IOException e) {
                e.printStackTrace();
            }


            setConnected(true);
            try {
                setStreams(socket.getInputStream(), socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // disconnect

    private static class LongOperationDisconnect extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            try {
//                serverSocket.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
