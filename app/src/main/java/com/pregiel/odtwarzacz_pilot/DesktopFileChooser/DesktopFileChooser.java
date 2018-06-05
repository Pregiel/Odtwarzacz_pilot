package com.pregiel.odtwarzacz_pilot.DesktopFileChooser;


import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.List;

public class DesktopFileChooser {

    private static final String DRIVES = "DRIVES";
    private Activity activity;

    private Dialog dialog;
    private View view;

    private String currentDirectory;

    private List<DesktopFileChooserItem> list;

    public DesktopFileChooser(Activity activity2, List<DesktopFileChooserItem> list2) {
        this.activity = activity2;
        this.list = list2;

        dialog = new Dialog(activity);

        view = activity.getLayoutInflater().inflate(R.layout.view_choosefile_desktop, null);

        ImageButton imageButton = view.findViewById(R.id.btnBack);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentDirectory.equals(DRIVES)) {
                    dialog.dismiss();
                } else if (currentDirectory.length() <= 3) {
                    openDrivesList();
                } else {
                    openDirectory(getPreviousDirectory());
                }

            }
        });
        currentDirectory = DRIVES;

        refreshDialog();


        dialog.setContentView(view);


    }

    private String getPreviousDirectory() {
        String previousDirectory = currentDirectory.substring(0, currentDirectory.lastIndexOf("\\"));
        if (previousDirectory.length() == 2) {
            return previousDirectory + "\\";
        }
        return previousDirectory;
    }

    public void showDialog() {
        dialog.show();
    }

    public void setList(List<DesktopFileChooserItem> list) {
        this.list = list;
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshDialog();
            }
        });
    }

    private void refreshDialog() {


        final ListView listView = view.findViewById(R.id.listView);

        DesktopFileChooserAdapter adapter = new DesktopFileChooserAdapter(activity, list);

        TextView currentPath = view.findViewById(R.id.txtCurrentPath);

        currentPath.setText(currentDirectory);


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(list.get(i).getPath());
                if (list.get(i).isDirectory()) {
                    openDirectory(list.get(i).getPath());
                } else {
                    Connection.sendMessage(Connection.FILECHOOSER_PLAYLIST_ADD, list.get(i).getPath());
                    dialog.dismiss();
                }
            }
        });

    }

    private void openDirectory(String path) {
        Connection.sendMessage(Connection.FILECHOOSER_DIRECTORY_TREE, path);
        currentDirectory = path;
    }

    private void openDrivesList() {
        Connection.sendMessage(Connection.FILECHOOSER_DRIVE_LIST);
        currentDirectory = DRIVES;
    }

    private String getExtension(String path) {
        return path.substring(path.lastIndexOf("."));
    }
}
