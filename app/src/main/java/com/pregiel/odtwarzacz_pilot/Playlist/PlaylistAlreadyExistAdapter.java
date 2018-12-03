package com.pregiel.odtwarzacz_pilot.Playlist;


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
import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.Utils;

import java.util.Arrays;
import java.util.List;

public class PlaylistAlreadyExistAdapter extends ArrayAdapter<String> {
    private List<String> selectedList;

    public PlaylistAlreadyExistAdapter(@NonNull Context context, @NonNull List<String> list, @Nullable List<String> selectedList) {
        super(context, 0, list);
        this.selectedList = selectedList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_choosefile, parent, false);

        String item = getItem(position);

        TextView fileName = convertView.findViewById(R.id.fileName);

        fileName.setText(item);

        if (selectedList.contains(item)) {
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.item_selected));
        } else {
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.background));
        }

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
