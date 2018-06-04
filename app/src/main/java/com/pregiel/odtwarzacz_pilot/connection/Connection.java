package com.pregiel.odtwarzacz_pilot.connection;

import android.os.AsyncTask;
import android.os.Build;

import com.pregiel.odtwarzacz_pilot.Views.PilotView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Pregiel on 09.04.2018.
 */

public abstract class Connection {

    public static final String PLAY = "PLAY";
    public static final String FORWARD = "FORWARD";
    public static final String BACKWARD = "BACKWARD";
    public static final String TIME = "TIME";
    public static final String VOLUME = "VOLUME";
    public static final String MUTE = "MUTE";
    public static final String UNMUTE = "UNMUTE";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String PLAYLIST_SEND = "PLAYLIST_SEND";
    public static final String PLAYLIST_UPDATE = "PLAYLIST_UPDATE";
    public static final String FORWARD_PRESSED = "FORWARD_PRESSED";
    public static final String FORWARD_RELEASED = "FORWARD_RELEASED";
    public static final String BACKWARD_PRESSED = "BACKWARD_PRESSED";
    public static final String BACKWARD_RELEASED = "BACKWARD_RELEASED";

    public static final String SEPARATOR = "::";

    private DataInputStream DIS;
    private DataOutputStream DOS;


    private boolean connected = false;


    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setStreams(PilotView view, InputStream inputStream, OutputStream outputStream) {
        this.DIS = new DataInputStream(inputStream);
        this.DOS = new DataOutputStream(outputStream);
        getMessage(view);
        view.setConnection(this);

        sendMessage(DEVICE_NAME, Build.MODEL);
    }

    public void getMessage(final PilotView view) {
        Thread connect = new Thread(new Runnable() {
            String msg_received = "";

            @Override
            public void run() {

                try {
                    msg_received = DIS.readUTF();
                    System.out.println(msg_received);
                    view.mediaController(msg_received);
                    getMessage(view);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        connect.setDaemon(true);
        connect.start();
    }


    public abstract void connect(PilotView view);

    public void sendMessage(Object... messages) {
        if (isConnected()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object o : messages) {
                stringBuilder.append(o).append(SEPARATOR);
            }
            new LongOperationSendMessage().execute(stringBuilder.toString());
        }
    }

    private class LongOperationSendMessage extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... strings) {
            try {
//                DOS = new DataOutputStream(socket.getOutputStream());
                DOS.writeUTF(strings[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
