package com.pregiel.odtwarzacz_pilot.Playlist.FileProperties;


import com.pregiel.odtwarzacz_pilot.R;

public class FileProperties {
    private String propValue;
    private int propName;

    public FileProperties(int propName, String propValue) {
        this.propName = propName;
        switch (propName) {
            case R.string.prop_bitrate:
                this.propValue = propValue + " kbps";
                break;

            case R.string.prop_channels:
                if (propValue.equals("2")) {
                    this.propValue = propValue + " (stereo)";
                } else if (propValue.equals("1")) {
                    this.propValue = propValue + " (mono)";
                } else {
                    this.propValue = propValue;
                }
                break;

            case R.string.prop_samplingrate:
                this.propValue = propValue + " Hz";
                break;

            default:
                this.propValue = propValue;
        }
    }

    public String getPropValue() {
        return propValue;
    }

    public int getPropName() {
        return propName;
    }
}
