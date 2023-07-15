package com.example.letswritetogether.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.letswritetogether.Activities.LoginActivity;
import com.example.letswritetogether.Activities.MainActivity;
import com.example.letswritetogether.Interfaces.FragmentChange_Callback;
import com.example.letswritetogether.R;
import com.example.letswritetogether.Utilities.MyDB;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class MenuFragment extends Fragment {
    private AppCompatImageView menu_IMG_background;
    private MaterialButton menu_BTN_log_out;
    private MaterialButton menu_BTN_create_new_song;
    private MaterialButton menu_BTN_favorites;
    private MaterialButton menu_BTN_participated_songs;
    private MaterialButton menu_BTN_my_songs;
    private MaterialButton menu_BTN_search_songs;
    private FragmentChange_Callback fragmentChange_callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        findViews(view);
        initViews();
        return view;
    }
    public void setFragmentChange_callback(FragmentChange_Callback callback) {
        this.fragmentChange_callback = callback;
    }

    private void initViews() {
        menu_BTN_log_out.setOnClickListener(v -> {
            logOut();
        });
        menu_BTN_create_new_song.setOnClickListener(v -> {
            if (fragmentChange_callback != null) {
                fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getNewSongFragment(),null);
            }
        });
        menu_BTN_favorites.setOnClickListener(v -> {
            if (fragmentChange_callback != null) {
                fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getMyFavoritesFragment(),null);
            }
        });
        menu_BTN_participated_songs.setOnClickListener(v -> {
            if (fragmentChange_callback != null) {
                fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getParticipatedSongsFragment(),null);
            }
        });
        menu_BTN_my_songs.setOnClickListener(v -> {
            if (fragmentChange_callback != null) {
                fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getMySongsFragment(),null);
            }
        });
        menu_BTN_search_songs.setOnClickListener(v -> {
            if (fragmentChange_callback != null) {
                fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getSongListFragment(),null);
            }
        });

    }
    private void logOut() {
//        MyDB.getInstance().detachListeners();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
    private void findViews(View view) {
        menu_IMG_background = view.findViewById(R.id.menu_IMG_background);
        menu_BTN_log_out = view.findViewById(R.id.menu_BTN_log_out);
        menu_BTN_create_new_song = view.findViewById(R.id.menu_BTN_create_new_song);
        menu_BTN_favorites = view.findViewById(R.id.menu_BTN_favorites);
        menu_BTN_participated_songs = view.findViewById(R.id.menu_BTN_participated_songs);
        menu_BTN_my_songs = view.findViewById(R.id.menu_BTN_my_songs);
        menu_BTN_search_songs = view.findViewById(R.id.menu_BTN_search_songs);
        Glide
                .with(this)
                .load(R.drawable.cream_paper1)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(menu_IMG_background);
    }
}
