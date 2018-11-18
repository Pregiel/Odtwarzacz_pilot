package com.pregiel.odtwarzacz_pilot;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.DesktopFileChooser.DesktopFileChooserItem;

import java.util.Arrays;
import java.util.List;


public class RecentFilesAdapter extends ArrayAdapter<String> {
    public RecentFilesAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_choosefile, parent, false);

        String item = getItem(position);

        TextView fileName = convertView.findViewById(R.id.fileName);

        fileName.setText(item);

        ImageView type = convertView.findViewById(R.id.type);

        if (Arrays.asList(MainActivity.SUPPORTED_AUDIO).contains(Utils.getExtension(item).toUpperCase())) {
            type.setImageResource(R.drawable.ic_audio);
        } else if (Arrays.asList(MainActivity.SUPPORTED_VIDEO).contains(Utils.getExtension(item).toUpperCase())) {
            type.setImageResource(R.drawable.ic_video);
        } else {
            type.setImageResource(R.drawable.ic_file);
        }

        return convertView;
    }
}
