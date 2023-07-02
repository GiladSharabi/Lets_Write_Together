package com.example.letswritetogether.Interfaces;

import androidx.fragment.app.Fragment;

import com.example.letswritetogether.Models.Song;

public interface FragmentChange_Callback {
    void onFragmentChange(Fragment fragment, Song song);
}
