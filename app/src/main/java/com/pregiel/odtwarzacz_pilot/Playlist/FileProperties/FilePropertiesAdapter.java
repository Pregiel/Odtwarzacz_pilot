package com.pregiel.odtwarzacz_pilot.Playlist.FileProperties;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.R;

import java.util.List;

public class FilePropertiesAdapter extends ArrayAdapter<FileProperties> {
    public FilePropertiesAdapter(@NonNull Context context, @NonNull List<FileProperties> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_fileproperties, parent, false);


        TextView propName = convertView.findViewById(R.id.propName);
        propName.setText(getItem(position).getPropName());

        TextView propValue = convertView.findViewById(R.id.propValue);
        propValue.setText(getItem(position).getPropValue());

        return convertView;
    }
}
