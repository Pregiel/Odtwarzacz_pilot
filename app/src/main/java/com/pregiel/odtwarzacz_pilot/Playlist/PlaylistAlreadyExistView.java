package com.pregiel.odtwarzacz_pilot.Playlist;


import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.DesktopFileChooser.DesktopFileChooserAdapter;
import com.pregiel.odtwarzacz_pilot.DesktopFileChooser.DesktopFileChooserItem;
import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.Utils;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistAlreadyExistView {

    private static final String DRIVES = "DRIVES";
    private Activity activity;

    private Dialog dialog;
    private View view;

    private String currentDirectory;

    private boolean multiSelect;

    private List<String> itemList, selectedItemsList;

    private PlaylistAlreadyExistAdapter adapter;

    public PlaylistAlreadyExistView(Activity activity) {
        this.activity = activity;
        this.itemList = new ArrayList<>();
        selectedItemsList = new ArrayList<>();

//        itemList.add(path);

        selectedItemsList.addAll(itemList);

        dialog = new Dialog(activity);

        view = activity.getLayoutInflater().inflate(R.layout.view_already_exist, null);

        final Button yesButton = view.findViewById(R.id.btn_yes);
        Button cancelButton = view.findViewById(R.id.btn_cancel);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList.size() > 0) {
                    Connection.sendMessage(Connection.FILECHOOSER_PLAYLIST_ADD_ALREADYEXIST, makeMessageFromList(selectedItemsList));
                }
                itemList.clear();
                selectedItemsList.clear();
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                itemList.clear();
                selectedItemsList.clear();
                dialog.dismiss();
            }
        });


        final ListView listView = view.findViewById(R.id.listView);
//        DesktopFileChooserAdapter adapter = new DesktopFileChooserAdapter(activity, treeItemsList, selectedItemsList);
        adapter = new PlaylistAlreadyExistAdapter(view.getContext(), itemList, selectedItemsList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = itemList.get(i);


                if (selectedItemsList.contains(item)) {
                    selectedItemsList.remove(item);
                    adapterView.getChildAt(i).setBackgroundColor(view.getResources().getColor(R.color.background));
                } else {
                    selectedItemsList.add(item);
                    adapterView.getChildAt(i).setBackgroundColor(view.getResources().getColor(R.color.item_selected));
                }

                if (selectedItemsList.size() > 0) {
                    yesButton.setEnabled(true);
                } else {
                    yesButton.setEnabled(false);
                }

            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(view);
    }

    public static String makeMessageFromList(List<String> list) {
        StringBuilder message = new StringBuilder();
        for (String item : list) {
            message.append(item).append(Connection.SEPARATOR);
        }
        return message.toString();
    }

    public void showDialog() {
        dialog.show();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void addToDialog(final String s) {
        System.out.println("ajajjajaj: " + s);
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemList.add(s);
                selectedItemsList.add(s);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
