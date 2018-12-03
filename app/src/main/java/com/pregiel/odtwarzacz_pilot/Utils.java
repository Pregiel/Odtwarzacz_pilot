package com.pregiel.odtwarzacz_pilot;


import android.content.Context;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.Toast;

import com.pregiel.odtwarzacz_pilot.DesktopFileChooser.DesktopFileChooserItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static String millisToString(double milis) {
        int seconds = (int) (milis / 1000) % 60;
        int minutes = (int) ((milis / (1000 * 60)) % 60);
        int hours = (int) ((milis / (1000 * 60 * 60)) % 24);

        if (hours == 0) {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
        return String.format(Locale.getDefault(), "%01d:%02d:%02d", hours, minutes, seconds);
    }


    public static String getExtension(String file) {
        return file.substring(file.lastIndexOf(".") + 1);
    }

    public static boolean isFile(String file) {
        return file.lastIndexOf(".") > 0;
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
