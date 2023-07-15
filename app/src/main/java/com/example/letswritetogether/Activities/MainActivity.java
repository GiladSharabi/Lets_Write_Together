package com.example.letswritetogether.Activities;

import static com.example.letswritetogether.Utilities.Constants.ERROR_FIND_USER;
import static com.example.letswritetogether.Utilities.Constants.SONG_CREATOR_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONG_NAME_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONG_TEXT_KEY;
import static com.example.letswritetogether.Utilities.Constants.UID_KEY;
import static com.example.letswritetogether.Utilities.Constants.WELCOME_NEW_USER;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.letswritetogether.Fragments.EditSongFragment;
import com.example.letswritetogether.Fragments.MyFavoritesFragment;
import com.example.letswritetogether.Fragments.MySongsFragment;
import com.example.letswritetogether.Fragments.ParticipatedSongsFragment;
import com.example.letswritetogether.Fragments.SearchSongsFragment;
import com.example.letswritetogether.Fragments.SongViewFragment;
import com.example.letswritetogether.Interfaces.DisplayedSongChange_Callback;
import com.example.letswritetogether.Interfaces.FindUser_Callback;
import com.example.letswritetogether.Interfaces.FragmentChange_Callback;
import com.example.letswritetogether.Fragments.MenuFragment;
import com.example.letswritetogether.Fragments.NewSongFragment;
import com.example.letswritetogether.Interfaces.Backable;
import com.example.letswritetogether.Interfaces.UpdateUserStars_Callback;
import com.example.letswritetogether.Models.Song;
import com.example.letswritetogether.Models.User;
import com.example.letswritetogether.R;
import com.example.letswritetogether.Utilities.DataManager;
import com.example.letswritetogether.Utilities.MyDB;
import com.example.letswritetogether.Utilities.SignalGenerator;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    // Fragments ---------------------------------------
    private Fragment prevFragment;
    private MenuFragment menuFragment;
    private NewSongFragment newSongFragment;
    private SongViewFragment songViewFragment;
    private EditSongFragment editSongFragment;
    private SearchSongsFragment searchSongsFragment;
    private MySongsFragment mySongsFragment;
    private MyFavoritesFragment myFavoritesFragment;
    private ParticipatedSongsFragment participatedSongsFragment;
    // -------------------------------------------------
    private AppCompatImageView main_IMG_background;
    private TextView main_TV_stars_count;
    private MaterialButton main_BTN_home;
    private FrameLayout main_frame_layout;
    private FragmentChange_Callback fragmentChange_callback = new FragmentChange_Callback() {
        @Override
        public void onFragmentChange(Fragment fragment, Song song) {
            if (fragment == null) {
                replaceFragment(prevFragment);
                return;
            }
            else if (fragment instanceof Backable) {
                prevFragment = fragment;
            }
            changeHomeButtonVisibility(fragment);
            if (song != null &&
                    (fragment instanceof SongViewFragment)) {
                DataManager.getInstance().setCurrentSongID(song.getSongID());
                Bundle bundle = new Bundle();
                bundle.putString(SONG_NAME_KEY, song.getTitle());
                bundle.putString(SONG_CREATOR_KEY, song.getCreatorName());
                bundle.putString(SONG_TEXT_KEY, song.getText());
                fragment.setArguments(bundle);
            }
            replaceFragment(fragment);
        }
    };
    private FindUser_Callback findUser_callback = new FindUser_Callback() {
        @Override
        public void user_found(User user) {
            DataManager.getInstance().setUser(user);
            int stars = user.getStars();
            main_TV_stars_count.setText(Integer.toString(stars));
            SignalGenerator.getInstance().toast("Welcome Back", Toast.LENGTH_SHORT);
        }
        @Override
        public void new_user_created(User user) {
            DataManager.getInstance().setUser(user);
            int stars = user.getStars();
            main_TV_stars_count.setText(Integer.toString(stars));

            SignalGenerator.getInstance().toast(WELCOME_NEW_USER, Toast.LENGTH_SHORT);
        }
        @Override
        public void error() {
            SignalGenerator.getInstance().toast(ERROR_FIND_USER, Toast.LENGTH_SHORT);
        }
    };
    private UpdateUserStars_Callback updateUserStars_callback = new UpdateUserStars_Callback() {
        @Override
        public void onStarsChanged() {
            main_TV_stars_count.setText(Integer.toString(DataManager.getInstance().getUser().getStars()));
        }
    };
    private DisplayedSongChange_Callback displayedSongChange_callback = new DisplayedSongChange_Callback() {
        @Override
        public void onChange(Song song) {
            songViewFragment.songChangeEvent(song);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragments();
        MyDB.getInstance().setFindUser_callback(findUser_callback);
        MyDB.getInstance().setUpdateUserStars_callback(updateUserStars_callback);
        MyDB.getInstance().setDisplayedSongChange_callback(displayedSongChange_callback);
        getUserFromDB(getIntent().getStringExtra(UID_KEY));
        findViews();
        initViews();
        beginTransactions();
    }
    private void changeHomeButtonVisibility(Fragment fragment) {
        if (fragment instanceof MenuFragment || fragment instanceof EditSongFragment
                || fragment instanceof NewSongFragment) {
            main_BTN_home.setVisibility(View.INVISIBLE);
        } else {
            main_BTN_home.setVisibility(View.VISIBLE);
        }
    }
    private void getUserFromDB(String userID) {
        MyDB.getInstance().getUserByID(userID);
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout, fragment)
                .addToBackStack(String.valueOf(fragment.getId()))
                .commit();
    }
    private void findViews() {
        main_IMG_background = findViewById(R.id.main_IMG_background);
        main_TV_stars_count = findViewById(R.id.main_TV_stars_count);
        main_frame_layout = findViewById(R.id.main_frame_layout);
        main_BTN_home = findViewById(R.id.main_BTN_home);
        Glide
                .with(this)
                .load(R.drawable.cream_paper1)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(main_IMG_background);
    }

    private void initViews() {
        main_BTN_home.setVisibility(View.INVISIBLE);
        main_BTN_home.setOnClickListener(v -> {
            if (fragmentChange_callback != null) {
                fragmentChange_callback.onFragmentChange(menuFragment, null);
            }
        });
    }

    private void initFragments() {
        menuFragment = new MenuFragment();
        newSongFragment = new NewSongFragment();
        songViewFragment = new SongViewFragment();
        editSongFragment = new EditSongFragment();
        searchSongsFragment = new SearchSongsFragment();
        mySongsFragment = new MySongsFragment();
        myFavoritesFragment = new MyFavoritesFragment();
        participatedSongsFragment = new ParticipatedSongsFragment();
        prevFragment = menuFragment;

        menuFragment.setFragmentChange_callback(fragmentChange_callback);
        newSongFragment.setFragmentChange_callback(fragmentChange_callback);
        songViewFragment.setFragmentChange_callback(fragmentChange_callback);
        editSongFragment.setFragmentChange_callback(fragmentChange_callback);
        searchSongsFragment.setFragmentChange_callback(fragmentChange_callback);
        mySongsFragment.setFragmentChange_callback(fragmentChange_callback);
        myFavoritesFragment.setFragmentChange_callback(fragmentChange_callback);
        participatedSongsFragment.setFragmentChange_callback(fragmentChange_callback);
    }

    private void beginTransactions() {
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_layout, menuFragment)
                .addToBackStack(String.valueOf(menuFragment.getId()))
                .commit();
    }
    @Override
    public void onBackPressed() {
        fragmentChange_callback.onFragmentChange(menuFragment,null);
    }
    public MenuFragment getMenuFragment() {
        return menuFragment;
    }
    public NewSongFragment getNewSongFragment() {
        return newSongFragment;
    }
    public SongViewFragment getSongViewFragment() {
        return songViewFragment;
    }
    public EditSongFragment getEditSongFragment() {
        return editSongFragment;
    }
    public SearchSongsFragment getSongListFragment() {
        return searchSongsFragment;
    }
    public MyFavoritesFragment getMyFavoritesFragment() {
        return myFavoritesFragment;
    }
    public ParticipatedSongsFragment getParticipatedSongsFragment() {
        return participatedSongsFragment;
    }
    public MySongsFragment getMySongsFragment() {
        return mySongsFragment;
    }

}