package com.pregiel.odtwarzacz_pilot.DesktopFileChooser;


import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;

import java.util.Arrays;
import java.util.List;

public class DesktopFileChooserAdapter extends ArrayAdapter<DesktopFileChooserItem> {
    private List<String> selectedList;

    public DesktopFileChooserAdapter(@NonNull Context context, @NonNull List<DesktopFileChooserItem> treeList, @Nullable List<String> selectedList) {
        super(context, 0, treeList);
        this.selectedList = selectedList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_choosefile, parent, false);

        DesktopFileChooserItem item = getItem(position);

        TextView fileName = convertView.findViewById(R.id.fileName);

        fileName.setText(item.getName());

        if (selectedList.contains(item.getPath())) {
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.item_selected));
        } else {
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.background));
        }


        ImageView type = convertView.findViewById(R.id.type);

        if (item.getPath().length() < 4) {
            type.setImageResource(R.drawable.ic_hard_drive);
        } else if (!item.isDirectory()) {
            if (Arrays.asList(MainActivity.SUPPORTED_AUDIO).contains(item.getExtension().toUpperCase())) {
                type.setImageResource(R.drawable.ic_audio);
            } else if (Arrays.asList(MainActivity.SUPPORTED_VIDEO).contains(item.getExtension().toUpperCase())) {
                type.setImageResource(R.drawable.ic_video);
            } else {
                type.setImageResource(R.drawable.ic_file);
            }
        }


        return convertView;
    }
}
