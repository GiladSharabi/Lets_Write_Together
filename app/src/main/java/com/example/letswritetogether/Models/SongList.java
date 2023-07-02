package com.example.letswritetogether.Models;

import java.util.ArrayList;

public class SongList {
    private ArrayList<Song> songList;


    public SongList() {
        songList = new ArrayList<>();
        songList.add(new Song("Test","ad","sdf","sf","hfd",true));
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public SongList setSongList(ArrayList<Song> songList) {
        this.songList = songList;
        return this;
    }
}
