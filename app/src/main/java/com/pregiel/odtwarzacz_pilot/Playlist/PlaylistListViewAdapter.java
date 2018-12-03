package com.pregiel.odtwarzacz_pilot.Playlist;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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

        final int index = position + 1;

        TextView nameText = convertView.findViewById(R.id.nameText);
        TextView durationText = convertView.findViewById(R.id.durationText);
        TextView queueText = convertView.findViewById(R.id.queueText);
        final CheckBox checkBox = convertView.findViewById(R.id.checkBox);


//        String[] name = MainActivity.getPlaylist().getPlaylist().get(position).split("\\\\");
        final String name = MainActivity.getPlaylist().getPlaylist().get(position).getTitle();

//        nameText.setText(name[name.length - 1]);
        nameText.setText(name);


        String duration = MainActivity.getPlaylist().getPlaylist().get(position).getDuration();
        durationText.setText(duration);

        if (MainActivity.getPlaylist().getPlaylistIndex() == position + 1) {
            nameText.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            durationText.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
        }

        queueText.setText(MainActivity.getPlaylist().getPlaylist().get(position).getQueueLabel());
        if (queueText.getText().equals("")) {
            queueText.setVisibility(View.GONE);
        } else {
            queueText.setVisibility(View.VISIBLE);
        }

        checkBox.setChecked(MainActivity.getPlaylist().getPlaylist().get(position).isEnable());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection.sendMessage(Connection.PLAYLIST_ENABLE, index, checkBox.isChecked());
            }
        });


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connection.sendMessage(Connection.PLAYLIST_PLAY, index);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                builder.setTitle(MainActivity.getPlaylist().getPlaylist().get(position).getTitle());
                builder.setItems(R.array.playlist_item_menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Connection.sendMessage(Connection.PLAYLIST_PLAY, index);
                                break;

                            case 1:
                                Connection.sendMessage(Connection.PLAYLIST_PROPERTIES, index);
                                break;

                            case 2:
                                Connection.sendMessage(Connection.QUEUE_ADD, index);
                                break;

                            case 3:
                                Connection.sendMessage(Connection.QUEUE_REMOVE, index);
                                break;

                            case 4:
                                checkBox.toggle();
                                Connection.sendMessage(Connection.PLAYLIST_ENABLE, index, checkBox.isChecked());
                                break;

                            case 5:
                                Connection.sendMessage(Connection.PLAYLIST_REMOVE, index);
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        ImageButton addQueue = convertView.findViewById(R.id.addQueue);
        addQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection.sendMessage(Connection.QUEUE_ADD, index);
            }
        });

        return convertView;
    }
}
