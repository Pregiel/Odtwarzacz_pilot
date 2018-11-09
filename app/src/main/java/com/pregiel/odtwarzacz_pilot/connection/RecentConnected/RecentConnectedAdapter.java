package com.pregiel.odtwarzacz_pilot.connection.RecentConnected;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pregiel.odtwarzacz_pilot.R;

import java.util.List;

public class RecentConnectedAdapter extends ArrayAdapter<RecentElement> {
    private List<RecentElement> elements;

    public RecentConnectedAdapter(Context context, List<RecentElement> elements) {
        super(context, 0, elements);
        this.elements = elements;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RecentElement element = elements.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_recentconnect, parent, false);

        TextView text = convertView.findViewById(R.id.text1);

        text.setText(element.getText());

        ImageView type = convertView.findViewById(R.id.type);

        switch (element.getConnectionType()) {
            case RecentElement.BT_CONNECTION:
                type.setImageResource(R.drawable.ic_bluetooth);
                break;

            default:
                type.setImageResource(R.drawable.ic_wifi);
                break;
        }

        return convertView;
    }
}
