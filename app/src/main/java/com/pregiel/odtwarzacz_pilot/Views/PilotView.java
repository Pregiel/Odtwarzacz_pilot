package com.pregiel.odtwarzacz_pilot.Views;


import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.Utils;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class PilotView {
//    private static Connection connection;

    private static final float PREVIEWHEIGHT = 0.6f;

    private boolean muted = false;

    private View view;

    public View getView() {
        return view;
    }

//    public ImageView imageView;

    private SeekBar timeSlider;

    private TextView timeCurrentText, timeTotalText, lblFilename, lblAuthor, lblNextFileName, lblNextAuthor;

    private View nextBar;

    private ImageButton playButton;

    private String currentFileLabel;

    private RelativeLayout previewLayout;

    private ImageView imgPreview;

    public SeekBar getTimeSlider() {
        return timeSlider;
    }

    public TextView getTimeCurrentText() {
        return timeCurrentText;
    }

    public TextView getTimeTotalText() {
        return timeTotalText;
    }

    public ImageView getImgPreview() {
        return imgPreview;
    }

    private boolean sendTime = false;

    public boolean isSendTime() {
        return sendTime;
    }

    private double totalTime;

    @SuppressLint("ClickableViewAccessibility")
    public View makeView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.view_pilot, container, false);

        if (!Connection.isConnected()) {
            Connection.showConnectionChooser(container);
        }

        lblFilename = view.findViewById(R.id.lbl_filename);
        lblAuthor = view.findViewById(R.id.lbl_author);

        lblNextFileName = view.findViewById(R.id.lbl_next_filename);
        lblNextAuthor = view.findViewById(R.id.lbl_next_author);

        imgPreview = view.findViewById(R.id.image_preview);
        previewLayout = view.findViewById(R.id.previewLayout);

        float previewLayoutsHeight = Resources.getSystem().getDisplayMetrics().heightPixels * PREVIEWHEIGHT;

        previewLayout.getLayoutParams().height = (int) Utils.convertPixelsToDp(previewLayoutsHeight, view.getContext());

        nextBar = view.findViewById(R.id.nextBar);

        timeTotalText = view.findViewById(R.id.lbl_time_total);
        timeCurrentText = view.findViewById(R.id.lbl_time_current);

        timeSlider = view.findViewById(R.id.slider_time);

        timeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (sendTime) {
                    Connection.sendMessage(Connection.TIME, (((double) seekBar.getProgress()) / 100));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sendTime = true;
                Connection.sendMessage(Connection.TIMESLIDER_START);
                if (timer != null) {
                    timer.cancel();
                }
                previewLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendTime = false;
                Connection.sendMessage(Connection.TIMESLIDER_STOP, (((double) seekBar.getProgress()) / 100));
                previewLayout.setVisibility(View.GONE);
            }
        });

        ImageButton prevButton = view.findViewById(R.id.btn_prev);
        ImageButton nextButton = view.findViewById(R.id.btn_next);
        ImageButton volUpButton = view.findViewById(R.id.btn_vol_up);
        ImageButton volDownButton = view.findViewById(R.id.btn_vol_down);
        playButton = view.findViewById(R.id.btn_play);

        final ImageView pressPrevButton = view.findViewById(R.id.circle_prev);
        final ImageView pressNextButton = view.findViewById(R.id.circle_next);
        final ImageView pressVolUpButton = view.findViewById(R.id.circle_vol_up);
        final ImageView pressVolDownButton = view.findViewById(R.id.circle_vol_down);

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
        ImageButton rerollButton = view.findViewById(R.id.btn_reroll);

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection.sendMessage(Connection.MUTE);
            }
        });

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection.sendMessage(Connection.RANDOM);
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection.sendMessage(Connection.REPEAT);
            }
        });

        rerollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection.sendMessage(Connection.REROLL);
            }
        });

        return view;
    }

    private View.OnTouchListener mediaButtonListener(
            final String pressedMessage, final String releasedMessage,
            final String clickedMessage, final ImageView pressEffect) {
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
                        MainActivity.getInstance().getmViewPager().setSwipeEnabled(false);
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
                        MainActivity.getInstance().getmViewPager().setSwipeEnabled(true);
                        return true;
                }
                return false;
            }
        };
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(final boolean muted) {
        this.muted = muted;
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton muteButton = view.findViewById(R.id.btn_mute);
                muteButton.setColorFilter(ContextCompat.getColor(view.getContext(), muted ? R.color.icons_on : R.color.icons_dark));
            }
        });
    }

    public void setRepeat(final boolean repeat) {
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton repeatButton = view.findViewById(R.id.btn_repeat);
                repeatButton.setColorFilter(ContextCompat.getColor(view.getContext(), repeat ? R.color.icons_on : R.color.icons_dark));
            }
        });
    }

    public void setRandom(final boolean random) {
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton randomButton = view.findViewById(R.id.btn_random);
                randomButton.setColorFilter(ContextCompat.getColor(view.getContext(), random ? R.color.icons_on : R.color.icons_dark));
            }
        });
    }

    public TextView getLblFilename() {
        return lblFilename;
    }

    public TextView getLblAuthor() {
        return lblAuthor;
    }

    public TextView getLblNextFileName() {
        return lblNextFileName;
    }

    public TextView getLblNextAuthor() {
        return lblNextAuthor;
    }

    public ImageButton getPlayButton() {
        return playButton;
    }

    public View getNextBar() {
        return nextBar;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    private Timer timer;

    public Timer getTimer() {
        return timer;
    }

    private boolean playing;

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    /**
     * @param value 0 - play  1 - pause
     */
    public void switchPlayButton(int value, double currentTime) {
        switch (value) {
            case 0:
//                playButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.getInstance().getApplicationContext(), R.drawable.ic_pause_circle));
                playButton.setImageResource(R.drawable.ic_pause_circle);
                playing = true;
                setTime(currentTime);
                break;

            default:
//                playButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.getInstance().getApplicationContext(), R.drawable.ic_play_circle));
                playButton.setImageResource(R.drawable.ic_play_circle);
                timer.cancel();
                playing = false;
        }
    }

    public void setTime(final double currentTime) {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        if (!sendTime) {

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                double time = currentTime;

                @Override
                public void run() {
                    if (playing && MainActivity.getPilotView().getTotalTime() > 0) {
                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getTimeCurrentText().setText(Utils.millisToString(time));
                                if ((int) MainActivity.getPilotView().getTotalTime() > 0) {
                                    getTimeSlider().setProgress((((int) time * 100) / (int) MainActivity.getPilotView().getTotalTime()));
                                }
                                time = time + 250;
                            }
                        });
                    }
                }
            }, (long) (currentTime % 250), 250);
        }
    }
}

