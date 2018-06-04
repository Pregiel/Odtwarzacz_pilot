package com.pregiel.odtwarzacz_pilot.Views;


import android.annotation.SuppressLint;
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

    private boolean muted = false;

    private View rootView;

    public View getView() {
        return rootView;
    }

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
                    if (isMuted()) {
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


    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }


}

