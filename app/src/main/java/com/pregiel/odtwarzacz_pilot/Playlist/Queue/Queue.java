package com.pregiel.odtwarzacz_pilot.Playlist.Queue;


import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.Playlist.PlaylistElement;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.ArrayList;
import java.util.List;

public class Queue {
    private Activity activity;
    private Dialog dialog;
    private View view;
    private QueueListAdapter queueListAdapter;

    private List<Integer> queueList;

    public Queue(Activity activity) {
        this.activity = activity;
        this.queueList = new ArrayList<>();

        dialog = new Dialog(activity);

        view = activity.getLayoutInflater().inflate(R.layout.view_queue, null);

        Button btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnClear = view.findViewById(R.id.btn_clear);
        btnClear.setEnabled(false);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection.sendMessage(Connection.QUEUE_CLEAR);
            }
        });

        queueListAdapter = new QueueListAdapter(activity, queueList);

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(queueListAdapter);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
    }

    public void showDialog() {
        dialog.show();
    }

    public void setEnableClear() {
        Button btnClear = view.findViewById(R.id.btn_clear);
        if (queueList.size() > 0) {
            btnClear.setEnabled(true);
        } else {
            btnClear.setEnabled(false);
        }
    }

    public void addToQueue(int value) {
        queueList.add(value);
    }

    public void clearQueue() {
        queueList.clear();
    }

    public int getItem(int index) {
        return queueList.get(index);
    }

    public void updatePlaylistView() {
        for (PlaylistElement playlistElement : MainActivity.getPlaylist().getPlaylist()) {
            playlistElement.getQueue().clear();
        }

        for (int i = 0; i < queueList.size(); i++) {
            MainActivity.getPlaylist().getPlaylist().get(queueList.get(i)).getQueue().add(i+1);
        }

        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.getPlaylistView().getPlaylistAdapter().notifyDataSetChanged();
                queueListAdapter.notifyDataSetChanged();
                setEnableClear();
            }
        });
    }
}
