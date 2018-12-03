package com.pregiel.odtwarzacz_pilot.connection.RecentConnected;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;
import com.pregiel.odtwarzacz_pilot.connection.WifiConnection;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

public class RecentConnectedAdapter extends ArrayAdapter<RecentElement> {
    private List<RecentElement> elements;

    public RecentConnectedAdapter(Context context, List<RecentElement> elements) {
        super(context, 0, elements);
        this.elements = elements;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final RecentElement element = elements.get(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_recentconnect, parent, false);


        final ProgressBar connectingBar = convertView.findViewById(R.id.connectingBar);
        final ImageButton removeButton = convertView.findViewById(R.id.removeButton);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("ostatnio wyb: " + element.getAddress());
                switch (element.getConnectionType()) {
                    case RecentElement.BT_CONNECTION:

                        break;

                    default:
                        WifiConnection.checkAndDo(new Runnable() {
                            @Override
                            public void run() {
                                connectingBar.setVisibility(View.VISIBLE);
                                removeButton.setVisibility(View.GONE);
                                WifiConnection.checkAndConnect(element.getAddress(), getContext(), new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                connectingBar.setVisibility(View.GONE);
                                                removeButton.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                }
            }
        });

        TextView device_name = convertView.findViewById(R.id.device_name);
        device_name.setText(element.getName());

        TextView address = convertView.findViewById(R.id.address);
        address.setText(element.getAddress());

        ImageView type = convertView.findViewById(R.id.type);

        switch (element.getConnectionType()) {
            case RecentElement.BT_CONNECTION:
                type.setImageResource(R.drawable.ic_bluetooth);
                break;

            default:
                type.setImageResource(R.drawable.ic_wifi);
                break;
        }


        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences recentPref = MainActivity.getInstance().getBaseContext().getSharedPreferences(Connection.RECENT_CONNECTION_TAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor recentPrefEditor = recentPref.edit();
                if (position == 0) {
                    String second = recentPref.getString("second", "none");
                    if (second.equals("none")) {
                        recentPrefEditor.remove("first");
                        recentPrefEditor.remove("first_name");
                    } else {
                        String second_name = recentPref.getString("second_name", "none");
                        recentPrefEditor.putString("first", second);
                        recentPrefEditor.putString("first_name", second_name);
                    }
                } else if (position == 1) {
                    recentPrefEditor.remove("second");
                    recentPrefEditor.remove("second_name");
                }
                recentPrefEditor.apply();
                elements.remove(element);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
