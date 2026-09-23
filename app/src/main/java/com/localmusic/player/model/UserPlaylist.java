package com.localmusic.player.model;

import java.util.ArrayList;
import java.util.List;

public class UserPlaylist {

    private String id;
    private String name;
    private List<Long> songIds;
    private long createdAt;
    private long updatedAt;

    public UserPlaylist() {
        songIds = new ArrayList<>();
    }

    public UserPlaylist(String id, String name, long createdAt, long updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.songIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getSongIds() {
        if (songIds == null) {
            songIds = new ArrayList<>();
        }
        return songIds;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
