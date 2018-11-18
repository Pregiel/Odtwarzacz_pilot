package com.pregiel.odtwarzacz_pilot.DesktopFileChooser;


import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.Utils;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DesktopFileChooser {

    private static final String DRIVES = "DRIVES";
    private Activity activity;

    private Dialog dialog;
    private View view;

    private String currentDirectory;

    private boolean multiSelect;

    private List<DesktopFileChooserItem> treeItemsList;
    private List<String> selectedItemsList;

    public DesktopFileChooser(Activity activity2, List<DesktopFileChooserItem> list2, boolean multiSelect, final String okMessage) {
        this.activity = activity2;
        this.treeItemsList = list2;
        this.multiSelect = multiSelect;
        this.selectedItemsList = new ArrayList<>();

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

        Button okButton = view.findViewById(R.id.btn_ok);
        Button cancelButton = view.findViewById(R.id.btn_cancel);

        okButton.setEnabled(false);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItemsList.size() > 0) {
                    Connection.sendMessage(okMessage, makeMessageFromList(selectedItemsList));
                }
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        currentDirectory = DRIVES;

        refreshDialog();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

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

    public void setTreeItemsList(List<DesktopFileChooserItem> treeItemsList) {
        this.treeItemsList = treeItemsList;
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshDialog();
            }
        });
    }

    private void refreshDialog() {
        final ListView listView = view.findViewById(R.id.listView);
        final Button okButton = view.findViewById(R.id.btn_ok);

        DesktopFileChooserAdapter adapter = new DesktopFileChooserAdapter(activity, treeItemsList, selectedItemsList);

        TextView currentPath = view.findViewById(R.id.txtCurrentPath);

        currentPath.setText(currentDirectory);


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DesktopFileChooserItem item = treeItemsList.get(i);
                System.out.println(item.getPath());

                if (item.isDirectory()) {
                    openDirectory(item.getPath());
                } else {
                    if (selectedItemsList.contains(item.getPath())) {
                        selectedItemsList.remove(item.getPath());
                        adapterView.getChildAt(i).setBackgroundColor(view.getResources().getColor(R.color.background));
                    } else {
                        if (!multiSelect) {
                            selectedItemsList.clear();
                        }
                        selectedItemsList.add(item.getPath());
                        adapterView.getChildAt(i).setBackgroundColor(view.getResources().getColor(R.color.item_selected));
                    }

                    if (selectedItemsList.size() > 0) {
                        okButton.setEnabled(true);
                    } else {
                        okButton.setEnabled(false);
                    }
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


    public static List<DesktopFileChooserItem> makeListFromMessage(String[] message) {
        List<DesktopFileChooserItem> dirs = new ArrayList<>();
        List<DesktopFileChooserItem> files = new ArrayList<>();

        for (int i = 1; i < message.length; i++) {
            if (Utils.isFile(message[i])) {
                if (Arrays.asList(MainActivity.SUPPORTED_AUDIO).contains(Utils.getExtension(message[i]).toUpperCase()) ||
                        Arrays.asList(MainActivity.SUPPORTED_VIDEO).contains(Utils.getExtension(message[i]).toUpperCase())) {
                    files.add(new DesktopFileChooserItem(message[i]));
                }
            } else {
                dirs.add(new DesktopFileChooserItem(message[i]));
            }
        }

        List<DesktopFileChooserItem> list = new ArrayList<>();
        list.addAll(dirs);
        list.addAll(files);

        return list;
    }

    public static String makeMessageFromList(List<String> list) {
        StringBuilder message = new StringBuilder();
        for (String item : list) {
            message.append(item).append(Connection.SEPARATOR);
        }
        return message.toString();
    }
}
