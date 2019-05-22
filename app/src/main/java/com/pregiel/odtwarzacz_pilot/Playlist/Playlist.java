package com.pregiel.odtwarzacz_pilot.Playlist;


import com.pregiel.odtwarzacz_pilot.MainActivity;
import com.pregiel.odtwarzacz_pilot.Playlist.Queue.Queue;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private List<PlaylistElement> playlist;
    private List<String> playlistTitles;
    private int playlistIndex;
    private Queue queue;

    public Playlist() {
        this.playlist = new ArrayList<>();
        this.playlistTitles = new ArrayList<>();
        this.queue = new Queue(MainActivity.getInstance());
    }

    public void makePlaylist(String[] message) {
        playlist.clear();
        for (int i = 1; i < message.length; i = i + 4) {
            playlist.add(new PlaylistElement(message[i], message[i + 1], message[i + 2], message[i + 3]));
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

    public Queue getQueue() {
        return queue;
    }

    public void setQueueFromMessage(String[] message) {
        queue.clearQueue();

        for (int i = 1; i < message.length; i++) {
            int playlistIndex = Integer.parseInt(message[i])-1;
            queue.addToQueue(playlistIndex);
        }

        queue.updatePlaylistView();
    }

    public void setNoPlaying() {

    }
}
