package com.pregiel.odtwarzacz_pilot.connection;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.AppCompatImageButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.pregiel.odtwarzacz_pilot.connection.RecentConnected.RecentConnectedAdapter;
import com.pregiel.odtwarzacz_pilot.connection.RecentConnected.RecentElement;
import com.pregiel.odtwarzacz_pilot.DesktopFileChooser.DesktopFileChooser;
import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Pregiel on 09.04.2018.
 */

public abstract class Connection {

    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String FORWARD = "FORWARD";
    public static final String BACKWARD = "BACKWARD";
    public static final String TIME = "TIME";
    public static final String DURATION = "DURATION";
    public static final String VOLUME = "VOLUME";
    public static final String MUTE = "MUTE";
    public static final String MUTE_ON = "MUTE_ON";
    public static final String MUTE_OFF = "MUTE_OFF";
    public static final String UNMUTE = "UNMUTE";
    public static final String RANDOM = "RANDOM";
    public static final String RANDOM_ON = "RANDOM_ON";
    public static final String RANDOM_OFF = "RANDOM_OFF";
    public static final String REPEAT = "REPEAT";
    public static final String REPEAT_ON = "REPEAT_ON";
    public static final String REPEAT_OFF = "REPEAT_OFF";
    public static final String REROLL = "REROLL";

    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String FILE_NAME = "FILE_NAME";
    public static final String NEXT_FILE = "NEXT_FILE";
    public static final String RECENT_FILES = "RECENT_FILES";

    public static final String TIMESLIDER_START = "TIMESLIDER_START";
    public static final String TIMESLIDER_STOP = "TIMESLIDER_STOP";

    public static final String PLAYLIST_SEND = "PLAYLIST_SEND";
    public static final String PLAYLIST_UPDATE = "PLAYLIST_UPDATE";
    public static final String PLAYLIST_PLAY = "PLAYLIST_PLAY";
    public static final String PLAYLIST_PLAYING_INDEX = "PLAYLIST_PLAYING_INDEX";
    public static final String PLAYLIST_TITLES = "PLAYLIST_TITLES";
    public static final String PLAYLIST_TITLE_INDEX = "PLAYLIST_TITLE_INDEX";

    public static final String FORWARD_PRESSED = "FORWARD_PRESSED";
    public static final String FORWARD_RELEASED = "FORWARD_RELEASED";
    public static final String FORWARD_CLICKED = "FORWARD_CLICKED";
    public static final String BACKWARD_PRESSED = "BACKWARD_PRESSED";
    public static final String BACKWARD_RELEASED = "BACKWARD_RELEASED";
    public static final String BACKWARD_CLICKED = "BACKWARD_CLICKED";

    public static final String VOLUME_UP_PRESSED = "VOLUME_UP_PRESSED";
    public static final String VOLUME_UP_RELEASED = "VOLUME_UP_RELEASED";
    public static final String VOLUME_UP_CLICKED = "VOLUME_UP_CLICKED";
    public static final String VOLUME_DOWN_PRESSED = "VOLUME_DOWN_PRESSED";
    public static final String VOLUME_DOWN_RELEASED = "VOLUME_DOWN_RELEASED";
    public static final String VOLUME_DOWN_CLICKED = "VOLUME_DOWN_CLICKED";

    public static final String FILECHOOSER_SHOW_PLAYLIST = "FILECHOOSER_SHOW_PLAYLIST";
    public static final String FILECHOOSER_SHOW_OPEN = "FILECHOOSER_SHOW_OPEN";
    public static final String FILECHOOSER_DIRECTORY_TREE = "FILECHOOSER_DIRECTORY_TREE";
    public static final String FILECHOOSER_DRIVE_LIST = "FILECHOOSER_DRIVE_LIST";
    public static final String FILECHOOSER_PLAY = "FILECHOOSER_PLAY";
    public static final String FILECHOOSER_PLAYLIST_ADD = "FILECHOOSER_PLAYLIST_ADD";

    public static final String SNAPSHOT = "SNAPSHOT";
    public static final String SNAPSHOT_REQUEST = "SNAPSHOT_REQUEST";

    public static final String NOTHING_PLAYING = "NOTHING_PLAYING";

    public static final String SEPARATOR = "::";

    public static final String RECENT_CONNECTION_TAG = "RECENT_CONNECTION_TAG";

    private static DataInputStream DIS;
    private static DataOutputStream DOS;


    private static boolean connected = false;


    public static boolean isConnected() {
        return connected;
    }

    public static void setConnected(boolean connected) {
        Connection.connected = connected;
    }

    public static void setStreams(InputStream inputStream, OutputStream outputStream) {
        DIS = new DataInputStream(inputStream);
        DOS = new DataOutputStream(outputStream);
        getMessage();
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        });
//        view.setConnection();

        sendMessage(DEVICE_NAME, Build.MODEL);
    }

    private static void getMessage() {
        Thread connect = new Thread(new Runnable() {
            String msg_received = "";

            @Override
            public void run() {

                try {
                    msg_received = DIS.readUTF();
//                    System.out.println(msg_received);
                    mediaController(msg_received);

                } catch (UTFDataFormatException e) {
                    e.printStackTrace();
                    getMessage();
                } catch (SocketException | EOFException e) {
                    e.printStackTrace();
                    disconnect();
                    Connection.showConnectionChooser(container);
                } catch (IOException e) {
                    e.printStackTrace();
                    disconnect();
                    Connection.showConnectionChooser(container);
                }

            }
        });
        connect.setDaemon(true);
        connect.start();
    }

    private static byte[] img;
    private static int length;
    private static boolean showPreview = false;

    public static void setShowPreview(boolean showPreview) {
        Connection.showPreview = showPreview;
    }

    private static void getImage() {
        Thread connect = new Thread(new Runnable() {
            byte[] img_received;

            @Override
            public void run() {

                try {
                    img_received = new byte[length];
                    DIS.readFully(img_received, 0, length);

                    img = img_received;
                    makeImage(img);
                    getMessage();

//                    if (showPreview) {
//                        Connection.sendMessage(Connection.SNAPSHOT_REQUEST);
//                    }

                } catch (SocketException | EOFException e) {
                    e.printStackTrace();
                    disconnect();
                    Connection.showConnectionChooser(container);
                } catch (IOException e) {
                    e.printStackTrace();
                    disconnect();
                    Connection.showConnectionChooser(container);
                }

            }
        });
        connect.setDaemon(true);
        connect.start();
    }

    private static void makeImage(byte[] bytes) {
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.getPilotView().getImgPreview().setImageBitmap(bitmap);

//                if (MainActivity.getPreviewView().getPreviewImageView() != null) {
//                    MainActivity.getPreviewView().getPreviewImageView().setImageBitmap(bitmap);
//                }
            }
        });
    }

    private static ServerSocket serverSocket;

    public static ServerSocket getServerSocket() {
        return serverSocket;
    }

    public static void setServerSocket(ServerSocket serverSocket) throws SocketException {
        serverSocket.setReuseAddress(true);
        Connection.serverSocket = serverSocket;
    }

    private static void disconnect() {
        setConnected(false);
        if (MainActivity.getPilotView().getTimer() != null) {
            MainActivity.getPilotView().getTimer().cancel();
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            DOS.close();
            DIS.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void sendMessage(Object... messages) {
        System.out.println("sending: " + Arrays.toString(messages));
        if (isConnected()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object o : messages) {
                stringBuilder.append(o).append(SEPARATOR);
            }
            new LongOperationSendMessage().execute(stringBuilder.toString());
        }
    }

    private static class LongOperationSendMessage extends AsyncTask<String, Void, Void> {


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

    public static void mediaController(String msg) {
        final String[] message = msg.split(Connection.SEPARATOR);
        System.out.println("received: " + msg);
        boolean getNextMessage = true;
        switch (message[0]) {
            case TIME:
                final double currentTimeMillis = Double.parseDouble(message[1]);

                ((Activity) MainActivity.getPilotView().getView().getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!MainActivity.getPilotView().isSendTime()) {
                            MainActivity.getPilotView().getTimeCurrentText().setText(Utils.millisToString(currentTimeMillis));
                            MainActivity.getPilotView().setTime(currentTimeMillis);
                            if ((int) MainActivity.getPilotView().getTotalTime() != 0) {
                                MainActivity.getPilotView().getTimeSlider().setProgress((((int) currentTimeMillis * 100) / (int) MainActivity.getPilotView().getTotalTime()));
                            }
                        }
                    }
                });
                break;

            case DURATION:
                MainActivity.getPilotView().setTotalTime(Double.parseDouble(message[1]));
                ((Activity) MainActivity.getPilotView().getView().getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.getPilotView().getTimeTotalText().setText(Utils.millisToString(
                                MainActivity.getPilotView().getTotalTime()
                        ));
                    }
                });

                if (message[2].equals("PLAYING")) {
                    MainActivity.getPilotView().setPlaying(true);
                } else {
                    MainActivity.getPilotView().setPlaying(false);
                }
                break;

            case VOLUME:
                final double volumeValue = Double.parseDouble(message[1]) * 100;

//                final SeekBar volumeSlider = MainActivity.getPilotView().getView().findViewById(R.id.volumeSlider);

//                ((Activity) MainActivity.getPilotView().getView().getContext()).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        volumeSlider.setProgress((int) volumeValue);
//                    }
//                });
                break;

            case MUTE_ON:
                MainActivity.getPilotView().setMuted(true);
                break;

            case MUTE_OFF:
                MainActivity.getPilotView().setMuted(false);
                break;

            case REPEAT_ON:
                MainActivity.getPilotView().setRepeat(true);
                break;

            case REPEAT_OFF:
                MainActivity.getPilotView().setRepeat(false);
                break;

            case RANDOM_ON:
                MainActivity.getPilotView().setRandom(true);
                break;

            case RANDOM_OFF:
                MainActivity.getPilotView().setRandom(false);
                break;

            case DEVICE_NAME:
                ((Activity) MainActivity.getPilotView().getView().getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.getPilotView().getView().getContext(), MainActivity.getPilotView().getView().getContext().getString(R.string.connected_with, message[1]), Toast.LENGTH_SHORT).show();
                    }
                });


                SharedPreferences recentPref = MainActivity.getInstance().getBaseContext().getSharedPreferences(RECENT_CONNECTION_TAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor recentPrefEditor = recentPref.edit();
                recentPrefEditor.putString("first_name", message[1]);
                recentPrefEditor.apply();


                break;

            case FILE_NAME:
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.getPilotView().getLblFilename().setText(message[1]);

                        MainActivity.getPilotView().getLblAuthor().setText(message.length > 2 ? message[2] : "");

                        MainActivity.getPilotView().noChosenFile(false);
                    }
                });
                break;

            case PLAY:
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final double currentTimeMillis = Double.parseDouble(message[1]);
                        MainActivity.getPilotView().switchPlayButton(0, currentTimeMillis);
                        MainActivity.getPilotView().getTimeCurrentText().setText(Utils.millisToString(currentTimeMillis));
                    }
                });
                break;

            case PAUSE:
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final double currentTimeMillis = Double.parseDouble(message[1].replaceAll("[^\\d.]", ""));
                        MainActivity.getPilotView().switchPlayButton(1, currentTimeMillis);
                        MainActivity.getPilotView().getTimeCurrentText().setText(Utils.millisToString(currentTimeMillis));
                    }
                });
                break;

            case NEXT_FILE:
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (message[1].equals("NONE")) {
                            MainActivity.getPilotView().getNextBar().setVisibility(View.GONE);
                        } else {
                            MainActivity.getPilotView().getNextBar().setVisibility(View.VISIBLE);
                            MainActivity.getPilotView().getLblNextFileName().setText(message[2]);

                            if (message.length > 3) {
                                MainActivity.getPilotView().getLblNextAuthor().setVisibility(View.VISIBLE);
                                MainActivity.getPilotView().getLblNextAuthor().setText(message[3]);
                            } else {
                                MainActivity.getPilotView().getLblNextAuthor().setVisibility(View.GONE);
                            }
                        }

                    }
                });
                break;

            case RECENT_FILES:
                MainActivity.getPilotView().setRecentFiles(message);
                break;

            case PLAYLIST_SEND:
                MainActivity.getPlaylist().makePlaylist(message);
                MainActivity.getPlaylistView().updatePlaylist();
                break;

            case PLAYLIST_PLAYING_INDEX:
                MainActivity.getPlaylist().setPlaylistIndex(Integer.parseInt(message[1]));
                MainActivity.getPlaylistView().updatePlaylist();
                break;

            case PLAYLIST_TITLES:
                MainActivity.getPlaylist().makePlaylistTitles(message);
                MainActivity.getPlaylistView().setPlaylistTitle(Integer.parseInt(message[1]));
                MainActivity.getPlaylistView().updatePlaylistTitles();
                break;

            case PLAYLIST_TITLE_INDEX:
                MainActivity.getPlaylistView().setPlaylistTitle(Integer.parseInt(message[1]));
                break;

//            case FILECHOOSER_SHOW:
//                MainActivity.getInstance().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        desktopFileChooser = new DesktopFileChooser(MainActivity.getInstance(), DesktopFileChooser.makeListFromMessage(message), true, FILECHOOSER_PLAYLIST_ADD);
//                        desktopFileChooser.showDialog();
//                    }
//                });
//                break;


            case FILECHOOSER_SHOW_PLAYLIST:
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        desktopFileChooser = new DesktopFileChooser(MainActivity.getInstance(), DesktopFileChooser.makeListFromMessage(message), true, FILECHOOSER_PLAYLIST_ADD);
                        desktopFileChooser.showDialog();
                    }
                });
                break;


            case FILECHOOSER_SHOW_OPEN:
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        desktopFileChooser = new DesktopFileChooser(MainActivity.getInstance(), DesktopFileChooser.makeListFromMessage(message), false, FILE_NAME);
                        desktopFileChooser.showDialog();
                    }
                });
                break;

            case FILECHOOSER_DIRECTORY_TREE:
                desktopFileChooser.setTreeItemsList(DesktopFileChooser.makeListFromMessage(message));
                break;
            case FILECHOOSER_DRIVE_LIST:
                desktopFileChooser.setTreeItemsList(DesktopFileChooser.makeListFromMessage(message));
                break;

            case SNAPSHOT:
                getNextMessage = false;
                length = Integer.valueOf(message[3]);
                getImage();
                if (MainActivity.getPilotView().isSendTime()) {
                    sendMessage(SNAPSHOT_REQUEST);
                }
//                System.out.println("SNAPSHOT " + message[1] + " " + message[2] + " " + Calendar.getInstance().getTimeInMillis());
                break;

            case NOTHING_PLAYING:
                MainActivity.getPilotView().noChosenFile(true);
                MainActivity.getPlaylist().setPlaylistIndex(-1);
                break;
        }

        if (getNextMessage) {
            getMessage();
        }
    }

    private static DesktopFileChooser desktopFileChooser;

    private static PopupWindow popupWindow;

    private static ViewGroup container;

    public static void showConnectionChooser(final ViewGroup container) {
        final LayoutInflater inflater = (LayoutInflater) MainActivity.getInstance().getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        Connection.container = container;

        final View popupView = inflater.inflate(R.layout.view_choose_connection, container, false);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(false);

        AppCompatImageButton wifiButton = popupView.findViewById(R.id.btnWifi);
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                showSearchDevicesView(0, container);
                WifiConnection.searchDevices();

            }
        });


        AppCompatImageButton btButton = popupView.findViewById(R.id.btnBt);
        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTConnection.searchForDevice(MainActivity.getInstance());
                BTConnection.connect();
            }
        });

//        Button usbButton = popupView.findViewById(R.id.btnUsb);
//
//        usbButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(MainActivity.getInstance().getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
//                        ContextCompat.checkSelfPermission(MainActivity.getInstance().getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
//                    UsbConnection.showSearchDevicesView();
//                }
//            }
//        });

        List<RecentElement> list = new ArrayList<>();

        SharedPreferences recentPref = MainActivity.getInstance().getBaseContext().getSharedPreferences(RECENT_CONNECTION_TAG, Context.MODE_PRIVATE);

        String first = recentPref.getString("first", "none");
        if (!first.equals("none")) {
            String first_name = recentPref.getString("first_name", "none");
            list.add(new RecentElement(first, first_name, 0));

            String second = recentPref.getString("second", "none");
            if (!second.equals("none")) {
                String second_name = recentPref.getString("second_name", "none");
                list.add(new RecentElement(second, second_name, 0));
            }
        }


        ListView recentConnected = popupView.findViewById(R.id.recentConnected);

        RecentConnectedAdapter adapter = new RecentConnectedAdapter(popupView.getContext(), list);

        recentConnected.setAdapter(adapter);

        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            }
        });
    }


    private static ListView foundedDevices;
    private static FoundedAdapter foundedAdapter;

    private static void showSearchDevicesView(final int type, final ViewGroup container) {
        final LayoutInflater inflater = (LayoutInflater) MainActivity.getInstance().getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.view_searching, container, false);

        WifiConnection.initFoundedList();

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(false);

        AppCompatImageButton cancel = popupView.findViewById(R.id.search_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case 0:
                        WifiConnection.stopSearching();
                        break;

                    case 1:

                        break;
                }
                popupWindow.dismiss();
                showConnectionChooser(container);
            }
        });

        foundedDevices = popupView.findViewById(R.id.founded_devices);

        foundedAdapter = new FoundedAdapter(popupView.getContext(), WifiConnection.getFoundedList());

        foundedDevices.setAdapter(foundedAdapter);


        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            }
        });
    }

    public static void setFoundedDevicesAdapter() {

        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                foundedAdapter.notifyDataSetChanged();

            }
        });
    }
}
