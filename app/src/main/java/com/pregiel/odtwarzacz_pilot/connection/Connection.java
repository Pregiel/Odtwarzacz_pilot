package com.pregiel.odtwarzacz_pilot.connection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.Utils;
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
    public static final String PLAYLIST_PLAY = "PLAYLIST_PLAY";
    public static final String FORWARD_PRESSED = "FORWARD_PRESSED";
    public static final String FORWARD_RELEASED = "FORWARD_RELEASED";
    public static final String BACKWARD_PRESSED = "BACKWARD_PRESSED";
    public static final String BACKWARD_RELEASED = "BACKWARD_RELEASED";

    public static final String SEPARATOR = "::";

    private DataInputStream DIS;
    private DataOutputStream DOS;


    private boolean connected = false;

    private PilotView pilotView;


    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setStreams(PilotView view, InputStream inputStream, OutputStream outputStream) {
        this.DIS = new DataInputStream(inputStream);
        this.DOS = new DataOutputStream(outputStream);
        getMessage();
        view.setConnection(this);

        pilotView = view;
        sendMessage(DEVICE_NAME, Build.MODEL);
    }

    public void getMessage() {
        Thread connect = new Thread(new Runnable() {
            String msg_received = "";

            @Override
            public void run() {

                try {
                    msg_received = DIS.readUTF();
                    System.out.println(msg_received);
                    mediaController(msg_received);
                    getMessage();


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

    public void mediaController(String msg) {
        final String[] message = msg.split(Connection.SEPARATOR);


        switch (message[0]) {
            case Connection.TIME:
                final TextView timeText = pilotView.getView().findViewById(R.id.timeView);
                final SeekBar timeSlider = pilotView.getView().findViewById(R.id.timeSlider);
                final double currentTimeMilis = Double.parseDouble(message[1]);
                final double mediaTimeMilis = Double.parseDouble(message[2]);

                final String newText = Utils.milisToString(currentTimeMilis) + "/" + Utils.milisToString(mediaTimeMilis);

                ((Activity) pilotView.getView().getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeText.setText(newText);
                        timeSlider.setProgress((((int) currentTimeMilis * 100) / (int) mediaTimeMilis));
                    }
                });
                break;

            case Connection.VOLUME:
                final double volumeValue = Double.parseDouble(message[1]) * 100;

                final SeekBar volumeSlider = pilotView.getView().findViewById(R.id.volumeSlider);

                ((Activity) pilotView.getView().getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        volumeSlider.setProgress((int) volumeValue);
                    }
                });
                break;

            case Connection.MUTE:
                pilotView.setMuted(true);
                break;

            case Connection.UNMUTE:
                pilotView.setMuted(false);
                break;

            case Connection.DEVICE_NAME:
                ((Activity) pilotView.getView().getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(pilotView.getView().getContext(), pilotView.getView().getContext().getString(R.string.connected_with, message[1]), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case Connection.PLAYLIST_SEND:
                MainActivity.getPlaylist().makePlaylist(message);
                MainActivity.getPlaylistView().updateListView();
                break;
        }
    }
}
