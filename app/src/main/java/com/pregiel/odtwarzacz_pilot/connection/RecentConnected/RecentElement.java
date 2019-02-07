package com.pregiel.odtwarzacz_pilot.connection.RecentConnected;


public class RecentElement {
    public static final int WIFI_CONNECTION = 0;
    public static final int BT_CONNECTION = 1;

    private String address, name;
    private int connectionType;

    public RecentElement(String address, String name, int connectionType) {
        this.address = address;
        this.name = name;
        this.connectionType = connectionType;
    }

    public RecentElement(String address, int connectionType) {
        this.address = address;
        this.connectionType = connectionType;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public int getConnectionType() {
        return connectionType;
    }
}
