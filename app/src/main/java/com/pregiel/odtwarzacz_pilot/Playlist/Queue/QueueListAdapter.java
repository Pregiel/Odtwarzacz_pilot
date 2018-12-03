package com.pregiel.odtwarzacz_pilot.Playlist.Queue;


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
import android.widget.ImageButton;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.Playlist.PlaylistElement;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.List;

public class QueueListAdapter extends ArrayAdapter<Integer> {

    public QueueListAdapter(Context context, List<Integer> queueList) {
        super(context, 0, queueList);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_queue, parent, false);

        final int index = MainActivity.getPlaylist().getQueue().getItem(position);

        TextView nameText = convertView.findViewById(R.id.nameText);

        final String name = MainActivity.getPlaylist().getPlaylist().get(index).getTitle();
        nameText.setText(name);

        ImageButton removeQueue = convertView.findViewById(R.id.removeQueue);
        removeQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection.sendMessage(Connection.QUEUE_REMOVE_INDEX, position + 1);
            }
        });

        return convertView;
    }
}
