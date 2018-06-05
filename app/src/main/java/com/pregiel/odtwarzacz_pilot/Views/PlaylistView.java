package com.pregiel.odtwarzacz_pilot.Views;


import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pregiel.odtwarzacz_pilot.DesktopFileChooser.DesktopFileChooser;
import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.Playlist.PlaylistListViewAdapter;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.ArrayList;

public class PlaylistView {

    private View view;
    private PlaylistListViewAdapter adapter;

    public View getView() {
        return view;
    }

    public View makeView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.view_playlist, container, false);
        if (adapter != null) {
            adapter.clear();
        }

        adapter = new PlaylistListViewAdapter(view.getContext(), MainActivity.getPlaylist().getPlaylist());

        ListView listView = view.findViewById(R.id.listview);
        listView.setAdapter(adapter);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connection.sendMessage(Connection.FILECHOOSER_SHOW);
            }
        });

        return view;
    }

    public void updateListView() {
        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
