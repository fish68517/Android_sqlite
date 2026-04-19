package com.localmusic.player.model;

import android.net.Uri;

public class Song {

    private final long id;
    private final String title;
    private final String artist;
    private final String album;
    private final long duration;
    private final String filePath;
    private final String folderName;
    private final long dateAdded;
    private final Uri contentUri;

    public Song(long id,
                String title,
                String artist,
                String album,
                long duration,
                String filePath,
                String folderName,
                long dateAdded,
                Uri contentUri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.filePath = filePath;
        this.folderName = folderName;
        this.dateAdded = dateAdded;
        this.contentUri = contentUri;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public long getDuration() {
        return duration;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFolderName() {
        return folderName;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public Uri getContentUri() {
        return contentUri;
    }
}
