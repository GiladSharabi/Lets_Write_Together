package com.example.letswritetogether.Models;

import static com.example.letswritetogether.Utilities.Constants.SONGS_CREATED_LIST_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONGS_FAVORITE_LIST_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONGS_PARTICIPATED_LIST_KEY;

public class User {
    private String userID = "";
    private int stars = 0;

    private UserSongsData songsCreated;
    private UserSongsData participatedSongs;
    private UserSongsData favoriteSongs;

    public User() {
    }
    public User(String userID, int stars) {
        this.userID = userID;
        this.stars = stars;
        this.songsCreated = new UserSongsData();
        this.participatedSongs = new UserSongsData();
        this.favoriteSongs = new UserSongsData();
        this.songsCreated.setName(SONGS_CREATED_LIST_KEY);
        this.participatedSongs.setName(SONGS_PARTICIPATED_LIST_KEY);
        this.favoriteSongs.setName(SONGS_FAVORITE_LIST_KEY);
    }

    public String getUserID() {
        return userID;
    }

    public User setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public int getStars() {
        return stars;
    }

    public User setStars(int stars) {
        this.stars = stars;
        return this;
    }

    public UserSongsData getSongsCreated() {
        return songsCreated;
    }

    public User setSongsCreated(UserSongsData songsCreated) {
        this.songsCreated = songsCreated;
        return this;
    }

    public UserSongsData getParticipatedSongs() {
        return participatedSongs;
    }

    public User setParticipatedSongs(UserSongsData participatedSongs) {
        this.participatedSongs = participatedSongs;
        return this;
    }

    public UserSongsData getFavoriteSongs() {
        return favoriteSongs;
    }

    public User setFavoriteSongs(UserSongsData favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
        return this;
    }

    public int getFavoriteSongIndex(String songID) {
        for (int i = 0; i < favoriteSongs.getList().size(); i++) {
            String temp = favoriteSongs.getList().get(i);
            if (songID.equals(temp))
                return i;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", stars=" + stars +
                ", songsCreated=" + songsCreated +
                ", participatedSongs=" + participatedSongs +
                ", favoriteSongs=" + favoriteSongs +
                '}';
    }
}
