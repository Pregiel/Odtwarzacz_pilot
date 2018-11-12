package com.pregiel.odtwarzacz_pilot.connection;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.RecentConnected.RecentElement;

import java.util.List;

public class FoundedAdapter extends ArrayAdapter<String> {
    private List<String> elements;

    public FoundedAdapter(Context context, List<String> elements) {
        super(context, 0, elements);
        this.elements = elements;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final String element = elements.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_recentconnect, parent, false);

        TextView text = convertView.findViewById(R.id.device_name);
        text.setText(element);

        TextView text2 = convertView.findViewById(R.id.address);
        text2.setText("");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiConnection.connect(element, getContext());

            }
        });
        ImageView type = convertView.findViewById(R.id.type);

        type.setImageResource(R.drawable.ic_wifi);

        ImageButton removeButton = convertView.findViewById(R.id.removeButton);
        removeButton.setVisibility(View.GONE);

        return convertView;
    }
}
