package com.pregiel.odtwarzacz_pilot.connection.RecentConnected;


public class RecentElement {
    public static final int WIFI_CONNECTION = 0;
    public static final int BT_CONNECTION = 1;

    private String text;
    private int connectionType;

    public RecentElement(String text, int connectionType) {
        this.text = text;
        this.connectionType = connectionType;
    }

    public String getText() {
        return text;
    }

    public int getConnectionType() {
        return connectionType;
    }
}
