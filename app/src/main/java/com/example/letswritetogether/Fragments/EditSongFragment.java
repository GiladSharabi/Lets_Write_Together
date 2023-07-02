package com.example.letswritetogether.Fragments;

import static com.example.letswritetogether.Utilities.Constants.SONG_UNDER_LOCK;
import static com.example.letswritetogether.Utilities.Constants.THE_TEXT_IS_EMPTY;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.letswritetogether.Activities.MainActivity;
import com.example.letswritetogether.Interfaces.FragmentChange_Callback;
import com.example.letswritetogether.Models.Song;
import com.example.letswritetogether.R;
import com.example.letswritetogether.Utilities.DataManager;
import com.example.letswritetogether.Utilities.MyDB;
import com.example.letswritetogether.Utilities.SignalGenerator;
import com.google.android.material.button.MaterialButton;

public class EditSongFragment extends Fragment {
    private TextView editSong_TV_songName;
    private TextView editSong_TV_songCreator;
    private TextView editSong_TV_songText;
    private EditText editSong_EText_newText;
    private MaterialButton editSong_BTN_saveChanges;
    private MaterialButton editSong_BTN_cancel;
    private FragmentChange_Callback fragmentChange_callback;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_song, container, false);
        findViews(view);
        initViews();

        return view;
    }
    public void setFragmentChange_callback(FragmentChange_Callback callback) {
        this.fragmentChange_callback = callback;
    }
    private void initViews() {
        editSong_BTN_saveChanges.setOnClickListener(v -> {
            Song song = DataManager.getInstance().getDisplayedSong();
            if (song.isLock_mode()) {
                SignalGenerator.getInstance().toast(SONG_UNDER_LOCK, Toast.LENGTH_SHORT);
                fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getSongViewFragment(), song);
            }
            else if (!editSong_EText_newText.getText().toString().isBlank()) {
                StringBuilder text = new StringBuilder(song.getText());
                text.append("\n\n"+editSong_EText_newText.getText().toString());
                MyDB.getInstance().updateSongText(DataManager.getInstance().getCurrentSongID(), text.toString());
                editSong_EText_newText.setText("");
                song.setText(text.toString());
                fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getSongViewFragment(), song);
            } else {
                SignalGenerator.getInstance().toast(THE_TEXT_IS_EMPTY, Toast.LENGTH_SHORT);
            }
        });
        editSong_BTN_cancel.setOnClickListener(v -> {
            MyDB.getInstance().changeSongAccess(DataManager.getInstance().getCurrentSongID(),true);
            fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getSongViewFragment(), null);
        });
        setTextViews();
    }

    private void findViews(View view) {
        editSong_TV_songName = view.findViewById(R.id.editSong_TV_songName);
        editSong_TV_songCreator = view.findViewById(R.id.editSong_TV_songCreator);
        editSong_TV_songText = view.findViewById(R.id.editSong_TV_songText);
        editSong_EText_newText = view.findViewById(R.id.editSong_EText_newText);
        editSong_BTN_saveChanges = view.findViewById(R.id.editSong_BTN_saveChanges);
        editSong_BTN_cancel = view.findViewById(R.id.editSong_BTN_cancel);
    }
    public void setTextViews() {
        Song song = DataManager.getInstance().getDisplayedSong();
        editSong_TV_songName.setText(song.getTitle());
        editSong_TV_songCreator.setText(song.getCreatorName());
        editSong_TV_songText.setText(song.getText());
    }
    @Override
    public void onPause() {
        super.onPause();
        editSong_EText_newText.setText("");
        MyDB.getInstance().changeSongAccess(DataManager.getInstance().getCurrentSongID(),true);
        Song song = DataManager.getInstance().getDisplayedSong();
        if (song != null)
            MyDB.getInstance().changeSongAccess(song.getSongID(),true);
    }
    @Override
    public void onStop() {
        super.onStop();
        fragmentChange_callback.onFragmentChange(((MainActivity)requireActivity()).getSongViewFragment(),null);
    }
}
