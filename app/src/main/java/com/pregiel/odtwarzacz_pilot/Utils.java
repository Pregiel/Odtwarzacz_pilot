package com.pregiel.odtwarzacz_pilot;


import com.pregiel.odtwarzacz_pilot.DesktopFileChooser.DesktopFileChooserItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static String milisToString(double milis) {
        int seconds = (int) (milis / 1000) % 60;
        int minutes = (int) ((milis / (1000 * 60)) % 60);
        int hours = (int) ((milis / (1000 * 60 * 60)) % 24);

        if (hours == 0) {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
        return String.format(Locale.getDefault(), "%01d:%02d:%02d", hours, minutes, seconds);
    }

    public static List<DesktopFileChooserItem> makeListFromMessage(String[] message) {
        List<DesktopFileChooserItem> dirs = new ArrayList<>();
        List<DesktopFileChooserItem> files = new ArrayList<>();

        for (int i = 1; i < message.length; i++) {
            if (isFile(message[i])) {
                if (Arrays.asList(MainActivity.SUPPORTED_AUDIO).contains(getExtension(message[i]).toUpperCase()) ||
                        Arrays.asList(MainActivity.SUPPORTED_VIDEO).contains(getExtension(message[i]).toUpperCase())) {
                    files.add(new DesktopFileChooserItem(message[i]));
                }
            } else {
                dirs.add(new DesktopFileChooserItem(message[i]));
            }
        }

        List<DesktopFileChooserItem> list = new ArrayList<>();
        list.addAll(dirs);
        list.addAll(files);

        return list;
    }

    public static String getExtension(String file) {
        return file.substring(file.lastIndexOf(".") + 1);
    }

    public static boolean isFile(String file) {
        return file.lastIndexOf(".") > 0;
    }
}
