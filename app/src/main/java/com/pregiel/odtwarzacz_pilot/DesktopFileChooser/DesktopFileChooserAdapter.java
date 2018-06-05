package com.pregiel.odtwarzacz_pilot.DesktopFileChooser;


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

public class DesktopFileChooserAdapter extends ArrayAdapter<DesktopFileChooserItem> {

    public DesktopFileChooserAdapter(@NonNull Context context, @NonNull List<DesktopFileChooserItem> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_choosefile, parent, false);

        TextView fileName = convertView.findViewById(R.id.fileName);

        fileName.setText(getItem(position).getName());


        return convertView;
    }
}
