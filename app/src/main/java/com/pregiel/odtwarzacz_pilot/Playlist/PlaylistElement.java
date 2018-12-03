package com.pregiel.odtwarzacz_pilot.Playlist;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistElement {
    private String title, duration;
    private List<Integer> queue;
    private boolean enable;

    public PlaylistElement(String title, String duration, String enable, String queue) {
        this.title = title;
        this.duration = duration;
        this.enable = Boolean.parseBoolean(enable);
        setQueue(queue);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<Integer> getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = new ArrayList<>();
        if (queue.contains("none")) {
            return;
        }

        String[] queueIndexes = queue.split(",");

        for (String queueIndex : queueIndexes) {
            this.queue.add(Integer.valueOf(queueIndex));
        }
    }

    public String getQueueLabel() {
        StringBuilder stringBuilder = new StringBuilder();
        if (queue.size() == 0) {
        } else if (queue.size() == 1) {
            stringBuilder.append("[").append(queue.get(0)).append("]");
        } else if (queue.size() < 4) {
            String prefix = "[";
            for (int ii : queue) {
                stringBuilder.append(prefix).append(ii);
                prefix = ", ";
            }
            stringBuilder.append("]");

        } else {
            stringBuilder.append("[")
                    .append(queue.get(0))
                    .append(" ... ")
                    .append(queue.get(queue.size() - 1))
                    .append("]");
        }

        return stringBuilder.toString();
    }
}
