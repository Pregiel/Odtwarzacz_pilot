package com.pregiel.odtwarzacz_pilot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pregiel.odtwarzacz_pilot.connection.BTConnection;
import com.pregiel.odtwarzacz_pilot.connection.Connection;
import com.pregiel.odtwarzacz_pilot.connection.WifiConnection;

import java.time.Duration;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

//    private enum ConnectionType {
//        NONE, WIFI, BT;
//    }

    public Connection connection;
//    private ConnectionType connectionType = ConnectionType.NONE;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeekBar timeSlider = findViewById(R.id.timeSlider);
        timeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                connection.sendMessage(Connection.TIME, (((double) seekBar.getProgress()) / 100));
            }
        });

        SeekBar volumeSlider = findViewById(R.id.volumeSlider);
        volumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isMuted) {
                    connection.sendMessage(Connection.UNMUTE);
                }
                connection.sendMessage(Connection.VOLUME, ((double) seekBar.getProgress()) / 100);

            }
        });

        ImageButton backwardButton = findViewById(R.id.btnBackward);
        ImageButton forwardButton = findViewById(R.id.btnForward);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.wifiConnect:
                WifiConnection wifiConnection = new WifiConnection();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
                    wifiConnection.connect(this);
//                    connectionType = ConnectionType.WIFI;
                }
                return true;
            case R.id.btConnect:
                BTConnection btConnection = new BTConnection();
                btConnection.searchForDevice(this);
                btConnection.connect(this);
//                connectionType = ConnectionType.BT;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void backward(View v) {
        connection.sendMessage(Connection.BACKWARD);
    }

    public void forward(View v) {
        connection.sendMessage(Connection.FORWARD);
    }

    public void play(View v) {
        connection.sendMessage(Connection.PLAY);
    }

    public void forwardButtonPressed() {
        connection.sendMessage(Connection.FORWARD_PRESSED);
    }

    public void forwardButtonReleased() {
        connection.sendMessage(Connection.FORWARD_RELEASED);
    }

    public void backwardButtonPressed() {
        connection.sendMessage(Connection.BACKWARD_PRESSED);
    }

    public void backwardButtonReleased() {
        connection.sendMessage(Connection.BACKWARD_RELEASED);
    }

    public void mediaController(String msg) {
        final String[] message = msg.split(Connection.SEPARATOR);


        switch (message[0]) {
            case Connection.TIME:
                final TextView timeText = findViewById(R.id.timeView);
                final SeekBar timeSlider = findViewById(R.id.timeSlider);
                final double currentTimeMilis = Double.parseDouble(message[1]);
                final double mediaTimeMilis = Double.parseDouble(message[2]);

                final String newText = milisToString(currentTimeMilis) + "/" + milisToString(mediaTimeMilis);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeText.setText(newText);
                        timeSlider.setProgress((((int) currentTimeMilis * 100) / (int) mediaTimeMilis));
                    }
                });
                break;

            case Connection.VOLUME:
                final double volumeValue = Double.parseDouble(message[1]) * 100;

                final SeekBar volumeSlider = findViewById(R.id.volumeSlider);

                runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.connected_with, message[1]), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case Connection.PLAYLIST_SEND:
                System.out.println(msg);
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
