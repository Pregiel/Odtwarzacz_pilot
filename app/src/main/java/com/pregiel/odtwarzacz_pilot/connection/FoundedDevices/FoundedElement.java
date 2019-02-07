package com.pregiel.odtwarzacz_pilot.connection.FoundedDevices;


import android.bluetooth.BluetoothDevice;

public class FoundedElement {

    private String address, name;


    public FoundedElement(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public FoundedElement(String address) {
        this.address = address;
        name = "";
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
