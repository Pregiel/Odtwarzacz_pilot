package com.pregiel.odtwarzacz_pilot.Playlist;


import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private List<PlaylistElement> playlist;
    private List<String> playlistTitles;
    private int playlistIndex;

    public Playlist() {
        this.playlist = new ArrayList<>();
        this.playlistTitles = new ArrayList<>();
    }

    public void makePlaylist(String[] message) {
        playlist.clear();
        for (int i = 1; i < message.length; i = i + 2) {
            playlist.add(new PlaylistElement(message[i], message[i+1]));
        }
    }

    public void makePlaylistTitles(String[] message) {
        playlistTitles.clear();
        for (int i = 2; i < message.length; i++) {
            playlistTitles.add(message[i]);
        }
    }

    public List<PlaylistElement> getPlaylist() {
        return playlist;
    }

    public List<String> getPlaylistTitles() {
        return playlistTitles;
    }

    public int getPlaylistIndex() {
        return playlistIndex;
    }

    public void setPlaylistIndex(int playlistIndex) {
        this.playlistIndex = playlistIndex;
    }
}
