package com.example.letswritetogether.Utilities;

import static com.example.letswritetogether.Utilities.Constants.SONGS_CREATED_LIST_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONGS_FAVORITE_LIST_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONGS_PARTICIPATED_LIST_KEY;

import android.util.Log;

import com.example.letswritetogether.Models.Song;
import com.example.letswritetogether.Models.User;
import com.example.letswritetogether.Models.UserSongsData;

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;

public class DataManager {


    private static DataManager instance = new DataManager();
    private User user;

    private String currentSongID = "";
    private ArrayList<Song> songList;

    public void removeSongFromFavorites(String currentSongID) {
        user.getFavoriteSongs().getList().remove(currentSongID);
    }
    public ArrayList<Song> getSongList() {
        return songList;
    }

    public DataManager setSongList(ArrayList<Song> songList) {
        this.songList = songList;
        return this;
    }

    public DataManager() {
        songList = new ArrayList<>();
    }

    public static DataManager getInstance() {
        return instance;
    }

    public void setUser(User user) {
        if (user.getFavoriteSongs() == null) {
            user.setFavoriteSongs(new UserSongsData());
            user.getFavoriteSongs().setName(SONGS_FAVORITE_LIST_KEY);
        }
        if (user.getSongsCreated() == null) {
            user.setSongsCreated(new UserSongsData());
            user.getSongsCreated().setName(SONGS_CREATED_LIST_KEY);
        }
        if (user.getParticipatedSongs() == null) {
            user.setParticipatedSongs(new UserSongsData());
            user.getParticipatedSongs().setName(SONGS_PARTICIPATED_LIST_KEY);
        }
        this.user = user;
    }

    public User getUser() {
        return user;
    }

//    public Song getCurrentSong() {
//        return currentSong;
//    }
//
//    public DataManager setCurrentSong(Song currentSong) {
//        this.currentSong = currentSong;
//        return this;
//    }

    public void addSong(Song song) {
        this.songList.add(song);
        Log.d("addSong",this.songList.toString());
    }

    public void songChangeEvent(Song song) {
        int i = findSongIndexByID(song.getSongID());
        if (i != -1) {
            this.songList.set(i, song);
        }
    }

    public int findSongIndexByID(String songID) {
        for (int i = 0; i < this.songList.size(); i++) {
            if (songID.equals(songList.get(i).getSongID())) {
                return i;
            }
        }
        return -1;
    }

    public Song getSongByID(String songID) { // return null if song doesn't found
        for (int i = 0; i < songList.size(); i++) {
            Song song = songList.get(i);
            if (songID.equals(song.getSongID()))
                return song;
        }
        return null;
    }
    public void songRemoveEvent(Song song) {
        int i = findSongIndexByID(song.getSongID());
        if (i != -1) {
            this.songList.remove(i);
        }
    }

    public String getCurrentSongID() {
        return currentSongID;
    }

    public DataManager setCurrentSongID(String currentSongID) {
        this.currentSongID = currentSongID;
        return this;
    }

    public boolean checkParticipationInSong(String currentSongID) {
        for (int i = 0; i < user.getParticipatedSongs().getList().size(); i++) {
            String temp = user.getParticipatedSongs().getList().get(i);
            if (currentSongID.equals(temp))
                return true;
        }
        return false;
    }

    public boolean isSongFavorite(String currentSongID) {
        for (int i = 0; i < user.getFavoriteSongs().getList().size(); i++) {
            String temp = user.getFavoriteSongs().getList().get(i);
            if (temp.equals(currentSongID))
                return true;
        }
        return false;
    }
    public ArrayList<Song> createUserSongList(String listName) {
        ArrayList<Song> list = new ArrayList<>();
        Log.d("AllSongs",this.songList.toString());
        if (listName == null) {
            for (int i = 0; i < songList.size(); i++) {
                Song song = songList.get(i);
                list.add(song);
            }
        }
        else if (listName.equals(SONGS_CREATED_LIST_KEY)) {
            String userID = this.user.getUserID();
            for (int i = 0; i < songList.size(); i++) {
                Song song = songList.get(i);
                if (userID.equals(song.getCreatorID())) {
                    list.add(song);
                }
            }
        }
        else if (listName.equals(SONGS_PARTICIPATED_LIST_KEY)) {
            for (int i = 0; i < this.user.getParticipatedSongs().getList().size(); i++) {
                String songID = user.getParticipatedSongs().getList().get(i);
                Song song = getSongByID(songID);
                if (song != null) {
                    list.add(song);
                }
            }
        }
        else if (listName.equals(SONGS_FAVORITE_LIST_KEY)) {
            for (int i = 0; i < user.getFavoriteSongs().getList().size(); i++) {
                String songID = user.getFavoriteSongs().getList().get(i);
                Song song = getSongByID(songID);
                if (song != null) {
                    list.add(song);
                }
            }
        }
        return list;
    }
    public Song getDisplayedSong() {
        return getSongByID(currentSongID);
    }
}
