package com.example.letswritetogether.Models;

public class Song implements Cloneable {
    private String songID = "";
    private String creatorID = "";
    private String creatorName = "";
    private String title = "";
    private String text = "";
    private boolean lock_mode = false;
    private boolean accessible;

    public Song() {
    }

    public Song(String songID, String creatorID, String creatorName, String title, String text, boolean accessible) {
        this.songID = songID;
        this.creatorID = creatorID;
        this.creatorName = creatorName;
        this.title = title;
        this.text = text;
        this.accessible = accessible;
        this.lock_mode = false;
    }
    @Override
    public Song clone() throws CloneNotSupportedException {
        return (Song)super.clone();
    }

    @Override
    public String toString() {
        return "Song{" +
                "songID='" + songID + '\'' +
                ", creatorID='" + creatorID + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", lock_mode=" + lock_mode +
                ", accessible=" + accessible +
                '}';
    }

    public String getCreatorID() {
        return creatorID;
    }

    public Song setCreatorID(String creatorID) {
        this.creatorID = creatorID;
        return this;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public Song setCreatorName(String creatorName) {
        this.creatorName = creatorName;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Song setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public Song setText(String text) {
        this.text = text;
        return this;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public Song setAccessible(boolean accessible) {
        this.accessible = accessible;
        return this;
    }
    public String getSongID() {
        return songID;
    }

    public Song setSongID(String songID) {
        this.songID = songID;
        return this;
    }

    public boolean isLock_mode() {
        return lock_mode;
    }

    public Song setLock_mode(boolean lock_mode) {
        this.lock_mode = lock_mode;
        return this;
    }
}
