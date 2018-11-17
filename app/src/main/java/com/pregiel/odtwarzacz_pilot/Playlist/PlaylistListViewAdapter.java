package com.pregiel.odtwarzacz_pilot.Playlist;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.List;

public class PlaylistListViewAdapter extends ArrayAdapter<PlaylistElement> {

    public PlaylistListViewAdapter(Context context, List<PlaylistElement> playlist) {
        super(context, 0, playlist);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_playlist, parent, false);



        TextView nameText = convertView.findViewById(R.id.nameText);
        TextView durationText = convertView.findViewById(R.id.durationText);

//        String[] name = MainActivity.getPlaylist().getPlaylist().get(position).split("\\\\");
        String name = MainActivity.getPlaylist().getPlaylist().get(position).getTitle();

//        nameText.setText(name[name.length - 1]);
        nameText.setText(name);


        String duration = MainActivity.getPlaylist().getPlaylist().get(position).getDuration();
        durationText.setText(duration);

        if (MainActivity.getPlaylist().getPlaylistIndex() == position + 1) {
            nameText.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            durationText.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
        }

        nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Connection.isConnected()) {
                    Connection.sendMessage(Connection.PLAYLIST_PLAY, position);
                }
            }
        });

        return convertView;
    }
}
