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
import com.pregiel.odtwarzacz_pilot.connection.FoundedDevices.FoundedElement;
import com.pregiel.odtwarzacz_pilot.connection.RecentConnected.RecentElement;

import java.util.List;

public class FoundedAdapter extends ArrayAdapter<FoundedElement> {
    private List<FoundedElement> elements;

    public FoundedAdapter(Context context, List<FoundedElement> elements) {
        super(context, 0, elements);
        this.elements = elements;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final FoundedElement element = elements.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_recentconnect, parent, false);

        TextView text = convertView.findViewById(R.id.device_name);
        TextView text2 = convertView.findViewById(R.id.address);

        if (element.getName().equals("")) {
            text.setText(element.getAddress());
            text2.setText("");
        } else {
            text.setText(element.getName());
            text2.setText(element.getAddress());
        }



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (Connection.getFoundedList().getMode()) {
                    case Connection.MODE_WIFI:
                        WifiConnection.connect(element.getAddress(), getContext());
                        break;

                    case Connection.MODE_BT:
                        BTConnection.connect(element);
                        break;

                }

            }
        });
        ImageView type = convertView.findViewById(R.id.type);

        switch (Connection.getFoundedList().getMode()) {
            case Connection.MODE_WIFI:
                type.setImageResource(R.drawable.ic_wifi);
                break;

            case Connection.MODE_BT:
                type.setImageResource(R.drawable.ic_bluetooth);
                break;

        }

        ImageButton removeButton = convertView.findViewById(R.id.removeButton);
        removeButton.setVisibility(View.GONE);

        return convertView;
    }
}
