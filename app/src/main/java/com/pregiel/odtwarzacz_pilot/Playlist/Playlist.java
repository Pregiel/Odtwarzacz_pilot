package com.pregiel.odtwarzacz_pilot.Playlist;


import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private List<String> playlist;

    public Playlist() {
        this.playlist = new ArrayList<>();
    }

    public void makePlaylist(String[] message) {
        playlist.clear();
        for (int i = 1; i < message.length; i++) {
            playlist.add(message[i]);
        }
    }

    public List<String> getPlaylist() {
        return playlist;
    }
}
