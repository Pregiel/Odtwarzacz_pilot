package com.pregiel.odtwarzacz_pilot.Views;


import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.R;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

public class PreviewView {

    private static final int SNAPSHOT_REQUEST_DELAY = 100;
    private View view;



    private ImageView previewImageView;

    public View makeView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.view_preview, container, false);

        previewImageView = view.findViewById(R.id.preview);

        return view;
    }

    public View getView() {
        return view;
    }

    public ImageView getPreviewImageView() {
        return previewImageView;
    }

}
