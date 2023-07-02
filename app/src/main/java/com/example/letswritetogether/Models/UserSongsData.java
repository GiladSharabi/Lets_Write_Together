package com.example.letswritetogether.Models;

import java.util.ArrayList;

public class UserSongsData {
    private String name = "";
    private ArrayList<String> list = new ArrayList<>();

    public UserSongsData() {
    }


    public UserSongsData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public UserSongsData setName(String name) {
        this.name = name;
        return this;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public UserSongsData setList(ArrayList<String> list) {
        this.list = list;
        return this;
    }

    @Override
    public String toString() {
        return "UserSongsData{" +
                "name='" + name + '\'' +
                ", list=" + list +
                '}';
    }

}
