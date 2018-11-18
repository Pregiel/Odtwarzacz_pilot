package com.pregiel.odtwarzacz_pilot.Views;


import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.Playlist.PlaylistListViewAdapter;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.ArrayList;
import java.util.List;

public class PlaylistView {

    private View view;
    private PlaylistListViewAdapter playlistAdapter;
    private ArrayAdapter<String> playlistTitlesAdapter;

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

        ListView listView = view.findViewById(R.id.listview);
        listView.setAdapter(playlistAdapter);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connection.sendMessage(Connection.FILECHOOSER_SHOW_PLAYLIST);
            }
        });

        return view;
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
}
