package com.example.letswritetogether.Fragments;

import static com.example.letswritetogether.Utilities.Constants.CREATE_NEW_SONG_COST;
import static com.example.letswritetogether.Utilities.Constants.NOT_ENOUGH_STARS_FOR_CREATE_SONG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.letswritetogether.Activities.MainActivity;
import com.example.letswritetogether.Interfaces.FragmentChange_Callback;
import com.example.letswritetogether.Interfaces.Backable;
import com.example.letswritetogether.Models.Song;
import com.example.letswritetogether.R;
import com.example.letswritetogether.Utilities.DataManager;
import com.example.letswritetogether.Utilities.MyDB;
import com.example.letswritetogether.Utilities.SignalGenerator;
import com.google.android.material.button.MaterialButton;

public class NewSongFragment extends Fragment implements Backable {
    private AppCompatImageView cns_IMG_background;
    private MaterialButton cns_BTN_cancel;
    private MaterialButton cns_BTN_create;
    private EditText cns_EText_creator;
    private EditText cns_EText_title;
    private EditText cns_EText_text;
    private FragmentChange_Callback fragmentChange_callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_new_song, container, false);
        findViews(view);
        initViews();
        return view;
    }

    public void setFragmentChange_callback(FragmentChange_Callback callback) {
        this.fragmentChange_callback = callback;
    }

    private void initViews() {
        cns_BTN_create.setOnClickListener(v -> {
            String userID = DataManager.getInstance().getUser().getUserID();
            String creatorName = cns_EText_creator.getText().toString();
            String title = cns_EText_title.getText().toString();
            String text = cns_EText_text.getText().toString();
            if (userID == null || userID.isBlank() ||
                    creatorName.isBlank() || title.isBlank() || text.isBlank()) {
                SignalGenerator.getInstance().toast(getContext().
                        getString(R.string.please_fill_in_all_the_fields), Toast.LENGTH_SHORT);
            } else { // all fields are ok - add song to DB
                if (DataManager.getInstance().getUser().getStars() >= CREATE_NEW_SONG_COST) {
                    Song song = new Song("",userID,creatorName,title,text,true);
                    MyDB.getInstance().addSongToDB(song);
                    fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getSongViewFragment(), song);
                } else {
                    SignalGenerator.getInstance().toast(NOT_ENOUGH_STARS_FOR_CREATE_SONG,Toast.LENGTH_SHORT);
                }
            }
        });
        cns_BTN_cancel.setOnClickListener(v -> {
            if (fragmentChange_callback != null) {
                fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getMenuFragment(), null);
            }
        });

    }
    private void clearAllFields() {
        cns_EText_creator.setText("");
        cns_EText_title.setText("");
        cns_EText_text.setText("");
    }
    private void findViews(View view) {
        cns_IMG_background = view.findViewById(R.id.cns_IMG_background);
        cns_EText_creator = view.findViewById(R.id.cns_EText_creator);
        cns_EText_title = view.findViewById(R.id.cns_EText_title);
        cns_EText_text = view.findViewById(R.id.cns_EText_text);
        cns_BTN_create = view.findViewById(R.id.cns_BTN_create);
        cns_BTN_cancel = view.findViewById(R.id.cns_BTN_cancel);
        Glide
                .with(this)
                .load(R.drawable.cream_paper4)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(cns_IMG_background);
    }

    @Override
    public void onResume() {
        super.onResume();
        clearAllFields();
    }
}
