package com.pregiel.odtwarzacz_pilot.connection;


import android.bluetooth.BluetoothDevice;

import com.pregiel.odtwarzacz_pilot.connection.FoundedDevices.FoundedElement;
import com.pregiel.odtwarzacz_pilot.connection.RecentConnected.RecentElement;

import java.util.ArrayList;
import java.util.List;

public class FoundedDevicesList {
    private List<FoundedElement> list;
    private int mode;

    public FoundedDevicesList(int mode) {
        list = new ArrayList<>();
        this.mode = mode;
    }

    public List<FoundedElement> getList() {
        return list;
    }

    public boolean addressInList(String address) {
        for (FoundedElement element : list) {
            if (element.getAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    public void add(String address) {
        list.add(new FoundedElement(address));
    }

    public void add(String address, String host) {
        if (!addressInList(address)) {
            list.add(new FoundedElement(address, host));
        } else {
            updateNameInList(address, host);
        }
    }

    public void clear() {
        list.clear();
    }

    public void removeIfInList(String address) {
        for (FoundedElement element : list) {
            if (element.getAddress().equals(address)) {
                list.remove(element);
            }
        }
    }

    public void updateNameInList(String address, String newName) {
        for (FoundedElement element : list) {
            if (element.getAddress().equals(address)) {
                element.setName(newName);
            }
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
