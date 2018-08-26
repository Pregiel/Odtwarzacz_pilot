package com.pregiel.odtwarzacz_pilot.Views;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class PilotView {
//    private static Connection connection;

    private boolean muted = false;

    private View view;

    public View getView() {
        return view;
    }

//    public ImageView imageView;

    private SeekBar timeSlider;

    private TextView timeCurrentText, timeTotalText;

//    public void setConnection(Connection connection) {
//        PilotView.connection = connection;
//    }
//
//    public static Connection getConnection() {
//        return connection;
//    }


    public SeekBar getTimeSlider() {
        return timeSlider;
    }

    public TextView getTimeCurrentText() {
        return timeCurrentText;
    }

    public TextView getTimeTotalText() {
        return timeTotalText;
    }

    @SuppressLint("ClickableViewAccessibility")
    public View makeView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.view_pilot, container, false);

        if (!Connection.isConnected()) {
            Connection.showConnectionChooser();
        }

        timeTotalText = view.findViewById(R.id.lbl_time_total);
        timeCurrentText = view.findViewById(R.id.lbl_time_current);

        timeSlider = view.findViewById(R.id.slider_time);
//        PilotView.view = view;
        timeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (connection != null) {
                Connection.sendMessage(Connection.TIME, (((double) seekBar.getProgress()) / 100));
//                }
            }
        });

//        timeSlider.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return false;
//            }
//        });

//        SeekBar volumeSlider = view.findViewById(R.id.volumeSlider);
//        volumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
////                if (connection != null) {
//                if (isMuted()) {
//                    Connection.sendMessage(Connection.UNMUTE);
//                }
//                Connection.sendMessage(Connection.VOLUME, ((double) seekBar.getProgress()) / 100);
//
//            }
//        });

        ImageButton prevButton = view.findViewById(R.id.btn_prev);
        ImageButton nextButton = view.findViewById(R.id.btn_next);
        ImageButton volUpButton = view.findViewById(R.id.btn_vol_up);
        ImageButton volDownButton = view.findViewById(R.id.btn_vol_down);
        ImageButton playButton = view.findViewById(R.id.btn_play);


        final ImageView pressPrevButton = view.findViewById(R.id.circle_prev);
        final ImageView pressNextButton = view.findViewById(R.id.circle_next);
        final ImageView pressVolUpButton = view.findViewById(R.id.circle_vol_up);
        final ImageView pressVolDownButton = view.findViewById(R.id.circle_vol_down);

//        imageView = view.findViewById(R.id.imageView);


        prevButton.setOnTouchListener(mediaButtonListener(
                Connection.BACKWARD_PRESSED,
                Connection.BACKWARD_RELEASED,
                Connection.BACKWARD_CLICKED,
                pressPrevButton));

        nextButton.setOnTouchListener(mediaButtonListener(
                Connection.FORWARD_PRESSED,
                Connection.FORWARD_RELEASED,
                Connection.FORWARD_CLICKED,
                pressNextButton));

        volUpButton.setOnTouchListener(mediaButtonListener(
                Connection.VOLUME_UP_PRESSED,
                Connection.VOLUME_UP_RELEASED,
                Connection.VOLUME_UP_CLICKED,
                pressVolUpButton));

        volDownButton.setOnTouchListener(mediaButtonListener(
                Connection.VOLUME_DOWN_PRESSED,
                Connection.VOLUME_DOWN_RELEASED,
                Connection.VOLUME_DOWN_CLICKED,
                pressVolDownButton));

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connection.sendMessage(Connection.PLAY);
            }
        });


        ImageButton muteButton = view.findViewById(R.id.btn_mute);
        ImageButton randomButton = view.findViewById(R.id.btn_random);
        ImageButton repeatButton = view.findViewById(R.id.btn_repeat);

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: zmiana kolorow
            }
        });

        return view;
    }

    private View.OnTouchListener mediaButtonListener(
            final String pressedMessage, final String releasedMessage, final String clickedMessage, final ImageView pressEffect) {
        return new View.OnTouchListener() {
            private static final int MAX_CLICK_DURATION = 500;
            private long startClickTime;
            private boolean pressed = false;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (pressed) {
                                    Connection.sendMessage(pressedMessage);
                                }
                            }
                        }, MAX_CLICK_DURATION);

                        pressEffect.setVisibility(View.VISIBLE);
                        pressed = true;
                        return true;

                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            Connection.sendMessage(clickedMessage);
                        } else {
                            Connection.sendMessage(releasedMessage);
                        }
                        pressEffect.setVisibility(View.INVISIBLE);
                        pressed = false;
                        return true;
                }
                return false;
            }
        };
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }


}

