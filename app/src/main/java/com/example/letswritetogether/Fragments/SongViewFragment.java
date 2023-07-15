package com.example.letswritetogether.Fragments;

import static com.example.letswritetogether.Utilities.Constants.SONG_ALREADY_EDIT_BY_USER;
import static com.example.letswritetogether.Utilities.Constants.SONG_CREATED_BY_YOU_ERROR;
import static com.example.letswritetogether.Utilities.Constants.SONG_CREATOR_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONG_NAME_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONG_TEXT_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONG_UNDER_EDIT;
import static com.example.letswritetogether.Utilities.Constants.SONG_UNDER_LOCK;
import static com.example.letswritetogether.Utilities.Constants.UNKNOWN_ERROR;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.letswritetogether.Activities.MainActivity;
import com.example.letswritetogether.Interfaces.FragmentChange_Callback;
import com.example.letswritetogether.Models.Song;
import com.example.letswritetogether.R;
import com.example.letswritetogether.Utilities.DataManager;
import com.example.letswritetogether.Utilities.MyDB;
import com.example.letswritetogether.Utilities.SignalGenerator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class SongViewFragment extends Fragment {
    private AppCompatImageView songView_IMG_background;
    private TextView songView_TV_songName;
    private TextView songView_TV_songCreator;
    private TextView songView_TV_songText;
    private MaterialButton songView_BTN_editSong;
    private MaterialButton songView_BTN_back;
    private ShapeableImageView songView_IMG_lock;
    private ShapeableImageView songView_IMG_favorite;
    private FragmentChange_Callback fragmentChange_callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_view, container, false);
        findViews(view);
        initViews();
        Bundle bundle = getArguments();
        songView_TV_songName.setText(bundle.getString(SONG_NAME_KEY));
        songView_TV_songCreator.setText(bundle.getString(SONG_CREATOR_KEY));
        songView_TV_songText.setText(bundle.getString(SONG_TEXT_KEY));
        return view;
    }

    public void setFragmentChange_callback(FragmentChange_Callback callback) {
        this.fragmentChange_callback = callback;
    }

    private void initViews() {
        setLockIcon();
        setFavoriteIcon();
        songView_BTN_editSong.setOnClickListener(v -> {
            if (isSongEditable()) {
                MyDB.getInstance().changeSongAccess(DataManager.getInstance().getCurrentSongID(), false);
                fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getEditSongFragment(), null);
            }
        });
        songView_BTN_back.setOnClickListener(v -> {
            fragmentChange_callback.onFragmentChange(null, null);
        });
        songView_IMG_favorite.setOnClickListener(v -> {
            String songID = DataManager.getInstance().getCurrentSongID();
            if (!DataManager.getInstance().isSongFavorite(songID)) {
                songView_IMG_favorite.setImageResource(R.drawable.heart);
//                DataManager.getInstance().addSongToFavorites(songID);
                MyDB.getInstance().addSongToUserListInDB(songID, DataManager.getInstance().getUser().getFavoriteSongs().getName());
            } else {
                songView_IMG_favorite.setImageResource(R.drawable.heart_empty);
//                DataManager.getInstance().removeSongFromFavorites(songID);
                int index = DataManager.getInstance().getUser().getFavoriteSongIndex(songID);
                if (index != -1)
                    MyDB.getInstance().removeSongFromUserFavoritesInDB(songID, index);
            }
        });
        songView_IMG_lock.setOnClickListener(v -> {
            Song currentSong = DataManager.getInstance().getDisplayedSong();
            if (currentSong.getCreatorID().equals(DataManager.getInstance().getUser().getUserID())) {
                if (currentSong.isLock_mode()) { // from lock to unlock
                    songView_IMG_lock.setImageResource(R.drawable.icon_unlock);
                    MyDB.getInstance().changeSongLockMode(currentSong.getSongID(), false);

                } else { // from unlock to lock
                    songView_IMG_lock.setImageResource(R.drawable.icon_lock);
                    MyDB.getInstance().changeSongLockMode(currentSong.getSongID(), true);
                }
            }

        });
    }
    private void setFavoriteIcon() {
        if (DataManager.getInstance().isSongFavorite(DataManager.getInstance().getCurrentSongID())) {
            songView_IMG_favorite.setImageResource(R.drawable.heart);
        } else {
            songView_IMG_favorite.setImageResource(R.drawable.heart_empty);
        }
    }
    private void setLockIcon() {
        Song song = DataManager.getInstance().getDisplayedSong();
        if (song != null) {
            if (song.getCreatorID().equals(DataManager.getInstance().getUser().getUserID())) {
                songView_IMG_lock.setVisibility(View.VISIBLE);
                if (song.isLock_mode()) {
                    songView_IMG_lock.setImageResource(R.drawable.icon_lock);
                } else {
                    songView_IMG_lock.setImageResource(R.drawable.icon_unlock);
                }
            } else {
                songView_IMG_lock.setVisibility(View.INVISIBLE);
            }
        }
    }
    private boolean isSongEditable() {
        Song song = DataManager.getInstance().getDisplayedSong();
        if (song == null) {
            SignalGenerator.getInstance().toast(UNKNOWN_ERROR, Toast.LENGTH_SHORT);
            return false;
        } else if (song.getCreatorID().equals(DataManager.getInstance().getUser().getUserID())) {
            SignalGenerator.getInstance().toast(SONG_CREATED_BY_YOU_ERROR, Toast.LENGTH_SHORT);
            return false;
        } else if (song.isLock_mode()) {
            SignalGenerator.getInstance().toast(SONG_UNDER_LOCK, Toast.LENGTH_SHORT);
            return false;
        } else if (DataManager.getInstance().checkParticipationInSong(song.getSongID())) {
            SignalGenerator.getInstance().toast(SONG_ALREADY_EDIT_BY_USER, Toast.LENGTH_SHORT);
            return false;
        } else if (!song.isAccessible()) {
            SignalGenerator.getInstance().toast(SONG_UNDER_EDIT, Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }
    private void findViews(View view) {
        songView_IMG_background = view.findViewById(R.id.songView_IMG_background);
        songView_TV_songName = view.findViewById(R.id.songView_TV_songName);
        songView_TV_songCreator = view.findViewById(R.id.songView_TV_songCreator);
        songView_TV_songText = view.findViewById(R.id.songView_TV_songText);
        songView_BTN_editSong = view.findViewById(R.id.songView_BTN_editSong);
        songView_BTN_back = view.findViewById(R.id.songView_BTN_back);
        songView_IMG_favorite = view.findViewById(R.id.songView_IMG_favorite);
        songView_IMG_lock = view.findViewById(R.id.songView_IMG_lock);
        Glide
                .with(this)
                .load(R.drawable.cream_paper4)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(songView_IMG_background);
    }
    public TextView getSongView_TV_songName() {
        return songView_TV_songName;
    }

    public TextView getSongView_TV_songCreator() {
        return songView_TV_songCreator;
    }

    public TextView getSongView_TV_songText() {
        return songView_TV_songText;
    }

    public void songChangeEvent(Song song) {
        songView_TV_songName.setText(song.getTitle());
        songView_TV_songCreator.setText(song.getCreatorName());
        songView_TV_songText.setText(song.getText());
    }
}
