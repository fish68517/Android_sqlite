package com.localmusic.player.model;

public class LibraryItem {

    private final LibraryCategory category;
    private final String title;
    private final String subtitle;
    private final String trailing;
    private final Song song;

    public LibraryItem(LibraryCategory category, String title, String subtitle, String trailing, Song song) {
        this.category = category;
        this.title = title;
        this.subtitle = subtitle;
        this.trailing = trailing;
        this.song = song;
    }

    public LibraryCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTrailing() {
        return trailing;
    }

    public Song getSong() {
        return song;
    }
}
