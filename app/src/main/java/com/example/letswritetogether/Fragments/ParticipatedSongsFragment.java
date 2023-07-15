package com.example.letswritetogether.Fragments;

import static com.example.letswritetogether.Utilities.Constants.SONGS_PARTICIPATED_LIST_KEY;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.letswritetogether.Activities.MainActivity;
import com.example.letswritetogether.Interfaces.FragmentChange_Callback;
import com.example.letswritetogether.Interfaces.Backable;
import com.example.letswritetogether.Models.Song;
import com.example.letswritetogether.R;
import com.example.letswritetogether.Utilities.DataManager;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class ParticipatedSongsFragment extends Fragment implements Backable {

    private ListView listView;
    private LinearLayout listItem_LL_song;
    private MaterialButton songList_BTN_search;
    private EditText songList_EText_search;
    private FragmentChange_Callback fragmentChange_callback;
    private ArrayList<Song> filterSongs;
    private ArrayAdapter<Song> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        findViews(view);
        initViews();
        return view;
    }

    public void setFragmentChange_callback(FragmentChange_Callback callback) {
        this.fragmentChange_callback = callback;
    }

    private void initViews() {
        filterSongs = DataManager.getInstance().createUserSongList(SONGS_PARTICIPATED_LIST_KEY);
        adapter = new ArrayAdapter<Song>(getContext(), R.layout.list_item_layout, R.id.listItem_TV_songName, filterSongs) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView listItem_TV_creatorName = view.findViewById(R.id.listItem_TV_creatorName);
                TextView listItem_TV_songName = view.findViewById(R.id.listItem_TV_songName);
                Song song = getItem(position);
                listItem_TV_creatorName.setText(song.getCreatorName());
                listItem_TV_songName.setText(song.getTitle());
                return view;
            }
        };
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click
                Song song = adapter.getItem(position);
                if (song != null && fragmentChange_callback != null) {
                    fragmentChange_callback.onFragmentChange(((MainActivity) requireActivity()).getSongViewFragment(), song);
                }
            }
        });
        songList_BTN_search.setOnClickListener(v -> {
            String searchText = songList_EText_search.getText().toString();
            filterTheSongs(searchText);
        });
    }
    private void filterTheSongs(String searchText) {
        ArrayList<Song> temp = new ArrayList<>();
        if (searchText.isEmpty()) {
            temp = DataManager.getInstance().createUserSongList(SONGS_PARTICIPATED_LIST_KEY);
        } else {
            for (Song song : DataManager.getInstance().getSongList()) {
                if (song.getTitle().toLowerCase().contains(searchText.toLowerCase()))
                    temp.add(song);
            }
        }
        filterSongs = temp;
        adapter.clear();
        adapter.addAll(filterSongs);
        adapter.notifyDataSetChanged();
    }

    public ArrayAdapter<Song> getAdapter() {
        return adapter;
    }

    private void findViews(View view) {
        listView = view.findViewById(R.id.songList_ListView_list);
        listItem_LL_song = view.findViewById(R.id.listItem_LL_song);
        songList_BTN_search = view.findViewById(R.id.songList_BTN_search);
        songList_EText_search = view.findViewById(R.id.songList_EText_search);
    }

    @Override
    public void onResume() {
        super.onResume();
        songList_EText_search.setText("");
        adapter.notifyDataSetChanged();
    }
}
