package com.pregiel.odtwarzacz_pilot.Views;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.Locale;

public class PilotView {
    private static Connection connection;


    private View rootView;


    public void setConnection(Connection connection) {
        PilotView.connection = connection;
    }

    public View makeView(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.view_pilot, container, false);

        SeekBar timeSlider = rootView.findViewById(R.id.timeSlider);
//        PilotView.rootView = rootView;
        timeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (connection != null) {
                    connection.sendMessage(Connection.TIME, (((double) seekBar.getProgress()) / 100));
                }
            }
        });

        SeekBar volumeSlider = rootView.findViewById(R.id.volumeSlider);
        volumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (connection != null) {
                    if (isMuted) {
                        connection.sendMessage(Connection.UNMUTE);
                    }
                    connection.sendMessage(Connection.VOLUME, ((double) seekBar.getProgress()) / 100);
                }
            }
        });

        ImageButton backwardButton = rootView.findViewById(R.id.btnBackward);
        ImageButton forwardButton = rootView.findViewById(R.id.btnForward);
        ImageButton playButton = rootView.findViewById(R.id.btnPlay);

        backwardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backwardButtonPressed();
                        break;
                    case MotionEvent.ACTION_UP:
                        backwardButtonReleased();
                        break;
                }
                return false;
            }
        });

        forwardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        forwardButtonPressed();
                        break;
                    case MotionEvent.ACTION_UP:
                        forwardButtonReleased();
                        break;
                }
                return false;
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });

        return rootView;
    }

    public void play() {
        if (connection != null) {
            connection.sendMessage(Connection.PLAY);
        }
    }

    public void forwardButtonPressed() {
        if (connection != null) {
            connection.sendMessage(Connection.FORWARD_PRESSED);
        }
    }

    public void forwardButtonReleased() {
        if (connection != null) {
            connection.sendMessage(Connection.FORWARD_RELEASED);
        }
    }

    public void backwardButtonPressed() {
        if (connection != null) {
            connection.sendMessage(Connection.BACKWARD_PRESSED);
        }
    }

    public void backwardButtonReleased() {
        if (connection != null) {
            connection.sendMessage(Connection.BACKWARD_RELEASED);
        }
    }

    public void mediaController(String msg) {
        final String[] message = msg.split(Connection.SEPARATOR);


        switch (message[0]) {
            case Connection.TIME:
                final TextView timeText = rootView.findViewById(R.id.timeView);
                final SeekBar timeSlider = rootView.findViewById(R.id.timeSlider);
                final double currentTimeMilis = Double.parseDouble(message[1]);
                final double mediaTimeMilis = Double.parseDouble(message[2]);

                final String newText = milisToString(currentTimeMilis) + "/" + milisToString(mediaTimeMilis);

                ((Activity) rootView.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeText.setText(newText);
                        timeSlider.setProgress((((int) currentTimeMilis * 100) / (int) mediaTimeMilis));
                    }
                });
                break;

            case Connection.VOLUME:
                final double volumeValue = Double.parseDouble(message[1]) * 100;

                final SeekBar volumeSlider = rootView.findViewById(R.id.volumeSlider);

                ((Activity) rootView.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        volumeSlider.setProgress((int) volumeValue);
                    }
                });
                break;

            case Connection.MUTE:
                isMuted = true;
                break;

            case Connection.UNMUTE:
                isMuted = false;
                break;

            case Connection.DEVICE_NAME:
                ((Activity) rootView.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(rootView.getContext(), rootView.getContext().getString(R.string.connected_with, message[1]), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case Connection.PLAYLIST_SEND:

                break;
        }
    }

    private boolean isMuted = false;

    private String milisToString(double milis) {
        int seconds = (int) (milis / 1000) % 60;
        int minutes = (int) ((milis / (1000 * 60)) % 60);
        int hours = (int) ((milis / (1000 * 60 * 60)) % 24);

        if (hours == 0) {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
        return String.format(Locale.getDefault(), "%01d:%02d:%02d", hours, minutes, seconds);
    }
}

