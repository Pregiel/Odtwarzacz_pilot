package com.pregiel.odtwarzacz_pilot.Views;


import android.app.Activity;
import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.DesktopFileChooser.DesktopFileChooser;
import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.Playlist.FileProperties.FileProperties;
import com.pregiel.odtwarzacz_pilot.Playlist.FileProperties.FilePropertiesAdapter;
import com.pregiel.odtwarzacz_pilot.Playlist.PlaylistListViewAdapter;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.ArrayList;
import java.util.List;

public class PlaylistView {

    private View view;
    private PlaylistListViewAdapter playlistAdapter;
    private ArrayAdapter<String> playlistTitlesAdapter;
    private ListView playlistList;

    public View getView() {
        return view;
    }

    public View makeView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.view_playlist, container, false);
        if (playlistAdapter != null) {
            playlistAdapter.clear();
        }

        Spinner playlistTitles = view.findViewById(R.id.selected_playlist);

        playlistTitlesAdapter = new ArrayAdapter<>(view.getContext(), R.layout.row, MainActivity.getPlaylist().getPlaylistTitles());

        playlistTitles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Connection.sendMessage(Connection.PLAYLIST_TITLE_INDEX, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        playlistTitles.setAdapter(playlistTitlesAdapter);

        playlistAdapter = new PlaylistListViewAdapter(view.getContext(), MainActivity.getPlaylist().getPlaylist());

        playlistList = view.findViewById(R.id.listview);
        playlistList.setAdapter(playlistAdapter);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connection.sendMessage(Connection.FILECHOOSER_SHOW_PLAYLIST);
            }
        });

        return view;
    }

    public void makePlaylist(final String[] message) {
        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.getPlaylist().makePlaylist(message);
                playlistAdapter.notifyDataSetChanged();
            }
        });
    }

    public void updatePlaylist() {
        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playlistAdapter.notifyDataSetChanged();
            }
        });
    }

    public void updatePlaylistTitles() {
        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playlistTitlesAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setPlaylistTitle(final int index) {
        final Spinner playlistTitles = view.findViewById(R.id.selected_playlist);

        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playlistTitles.setSelection(index);
                playlistTitlesAdapter.notifyDataSetChanged();
            }
        });

    }

    public void selectItem(final int index, final boolean value) {
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = view.findViewById(R.id.listview);
                CheckBox checkBox = listView.getChildAt(index).findViewById(R.id.checkBox);

                checkBox.setChecked(value);
            }
        });
    }

    public void showPropertiesWindow(final String[] message) {
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = new Dialog(MainActivity.getInstance());
                View propView = MainActivity.getInstance().getLayoutInflater().inflate(R.layout.view_fileproperties, null);

                FilePropertiesAdapter filePropertiesAdapter = new FilePropertiesAdapter(view.getContext(), messageToFileProperties(message));

                ListView listView = propView.findViewById(R.id.propListview);
                listView.setAdapter(filePropertiesAdapter);

                Button propButton = propView.findViewById(R.id.propButton);
                propButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(propView);
                dialog.show();
            }
        });
    }

    private List<FileProperties> messageToFileProperties(String[] message) {
        List<FileProperties> list = new ArrayList<>();

        list.add(new FileProperties(R.string.prop_name, message[1]));
        list.add(new FileProperties(R.string.prop_path, message[2]));
        list.add(new FileProperties(R.string.prop_duration, message[3]));

        if (message[4].equals("audio")) {
            list.add(new FileProperties(R.string.prop_artist, message[5]));
            list.add(new FileProperties(R.string.prop_album, message[6]));
            if (!message[7].equals("-1"))
                list.add(new FileProperties(R.string.prop_bitrate, message[7]));
            list.add(new FileProperties(R.string.prop_channels, message[8]));
            list.add(new FileProperties(R.string.prop_samplingrate, message[9]));
        } else if (message[4].equals("video")) {
            if (!message[5].equals("-1"))
                list.add(new FileProperties(R.string.prop_bitrate, message[5]));
            list.add(new FileProperties(R.string.prop_width, message[6]));
            list.add(new FileProperties(R.string.prop_height, message[7]));
            list.add(new FileProperties(R.string.prop_framerate, message[8]));
        }

        return list;
    }

    public PlaylistListViewAdapter getPlaylistAdapter() {
        return playlistAdapter;
    }
}
