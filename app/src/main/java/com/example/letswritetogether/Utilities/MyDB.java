package com.example.letswritetogether.Utilities;

import static com.example.letswritetogether.Utilities.Constants.CREATE_NEW_SONG_COST;
import static com.example.letswritetogether.Utilities.Constants.EDIT_SONG_REWARD;
import static com.example.letswritetogether.Utilities.Constants.INITIAL_STARS_COUNT;
import static com.example.letswritetogether.Utilities.Constants.SONGS_CREATED_LIST_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONGS_FAVORITE_LIST_KEY;
import static com.example.letswritetogether.Utilities.Constants.SONGS_KEY_DB;
import static com.example.letswritetogether.Utilities.Constants.SONGS_PARTICIPATED_LIST_KEY;
import static com.example.letswritetogether.Utilities.Constants.USERS_KEY_DB;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.letswritetogether.Interfaces.DisplayedSongChange_Callback;
import com.example.letswritetogether.Interfaces.FindUser_Callback;
import com.example.letswritetogether.Interfaces.UpdateUserStars_Callback;
import com.example.letswritetogether.Models.Song;
import com.example.letswritetogether.Models.User;
import com.example.letswritetogether.Models.UserSongsData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyDB {

    private static MyDB instance = new MyDB();
    private FirebaseDatabase db;
    DatabaseReference songsRef;
    DatabaseReference usersRef;
    FindUser_Callback findUser_callback;
    UpdateUserStars_Callback updateUserStars_callback;
    DisplayedSongChange_Callback displayedSongChange_callback;
    public MyDB() {
        db = FirebaseDatabase.getInstance();
        songsRef = db.getReference(SONGS_KEY_DB);
        usersRef = db.getReference(USERS_KEY_DB);
        registerForSongUpdates();
        registerForUserUpdates();
    }

    private void registerForUserUpdates() {
        usersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getUserID().equals(DataManager.getInstance().getUser().getUserID())) {
//                    SignalGenerator.getInstance().toast("ChildChanged",Toast.LENGTH_SHORT);
                    Log.d("UserChanged", user.toString());
                    DataManager.getInstance().setUser(user);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void registerForSongUpdates() {
        songsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Song song = snapshot.getValue(Song.class);
                DataManager.getInstance().addSong(song);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Song song = snapshot.getValue(Song.class);
                DataManager.getInstance().songChangeEvent(song);
                if (displayedSongChange_callback != null) {
                    displayedSongChange_callback.onChange(song);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Song song = snapshot.getValue(Song.class);
                DataManager.getInstance().songRemoveEvent(song);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ArrayList<Song> getAllSongsFromDB() {
        ArrayList<Song> arr= new ArrayList<>();
        songsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                    Song song = songSnapshot.getValue(Song.class);
//                    Log.d("TheSong", song.toString());
                    arr.add(song);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return arr;
    }

    public void addSongToDB(Song song) {
        String songKey = songsRef.push().getKey(); // Generate a unique key for the song
        song.setSongID(songKey);
        songsRef.child(songKey).setValue(song)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        DataManager.getInstance().setCurrentSongID(songKey);
                        addSongToUserListInDB(songKey, SONGS_CREATED_LIST_KEY);
                        SignalGenerator.getInstance().toast("Song added successfully to DB", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        SignalGenerator.getInstance().toast("Failed to add song to DB", Toast.LENGTH_SHORT);
                    }
                });
    }


    public void addSongToUserListInDB(String songKey, String listName) {

        int stars = DataManager.getInstance().getUser().getStars();
        User user = DataManager.getInstance().getUser();
        if (listName.equals(SONGS_CREATED_LIST_KEY)) {
            user.setStars(stars - CREATE_NEW_SONG_COST);
            user.getSongsCreated().getList().add(songKey);
            updateUserStars_callback.onStarsChanged();
        }
        else if (listName.equals(SONGS_PARTICIPATED_LIST_KEY)) {
            user.setStars(stars + EDIT_SONG_REWARD);
            user.getParticipatedSongs().getList().add(songKey);
            updateUserStars_callback.onStarsChanged();
        }
        else if (listName.equals(SONGS_FAVORITE_LIST_KEY)) {
            user.getFavoriteSongs().getList().add(songKey);
        }
        else
            return;
        setUserToDB();

    }

    private void setUserToDB() {
        usersRef.child(DataManager.getInstance().getUser().getUserID())
                .setValue(DataManager.getInstance().getUser())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public static MyDB getInstance() {
        return instance;
    }

    public FirebaseDatabase getDb() {
        return db;
    }

    public DatabaseReference getSongsRef() {
        return songsRef;
    }

    public DatabaseReference getUsersRef() {
        return usersRef;
    }


    public void hardCodedSongsToDB() {
        Song song1 = new Song("", "EJ7TwFWBAlaCjwp8WYf5rEW3tPq1", "Bon Jovi", "It's My Life", "This ain't a song for the broken-hearted\n" +
                "No silent prayer for faith-departed", true);
        Song song2 = new Song("", "EJ7TwFWBAlaCjwp8WYf5rEW3tPq1", "Imagine Dragons", "Demons", "When the days are cold\n" +
                "And the cards all fold\n" +
                "And the saints we see are all made of gold", true);
        Song song3 = new Song("", "EJ7TwFWBAlaCjwp8WYf5rEW3tPq1", "Maroon 5", "Girls Like You", "spent 24 hours, I need more hours with you\n" +
                "You spent the weekend getting even, ooh\n" +
                "We spent the late nights making things right between us", true);
        Log.d("numOfLetters", Integer.toString(song1.getText().length()));
        Log.d("numOfLetters", Integer.toString(song2.getText().length()));
        Log.d("numOfLetters", Integer.toString(song3.getText().length()));
    }

    public void getUserByID(String userID) {
        if (findUser_callback != null) {
            usersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) { // user already exist
                        User user = snapshot.getValue(User.class);
                        findUser_callback.user_found(user);
                    } else { // user doesn't exist
                        createNewUserInDB(userID);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    findUser_callback.error();
                }
            });
        }
    }

    private void createNewUserInDB(String userID) {
        User newUser = new User(userID,INITIAL_STARS_COUNT);
        usersRef.child(userID).setValue(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        findUser_callback.new_user_created(newUser);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        findUser_callback.error();
                    }
                });
    }

    public MyDB setFindUser_callback(FindUser_Callback findUser_callback) {
        this.findUser_callback = findUser_callback;
        return this;
    }
    public MyDB setUpdateUserStars_callback(UpdateUserStars_Callback updateUserStars_callback) {
        this.updateUserStars_callback = updateUserStars_callback;
        return this;
    }

    public void changeSongAccess(String currentSongID,Boolean isAccessible) {
        songsRef.child(currentSongID).child("accessible").setValue(isAccessible);
    }

//    public void updateUserStars(String userID, int stars) {
//        usersRef.child(userID).child("stars").setValue(stars)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        updateUserStars_callback.onStarsChanged();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//    }

    public void updateSongText(String currentSongID, String newText) {
        songsRef.child(currentSongID).child("text").setValue(newText)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        addSongToUserListInDB(currentSongID, SONGS_PARTICIPATED_LIST_KEY);
                        SignalGenerator.getInstance().toast("Song edited successfully in DB",Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        SignalGenerator.getInstance().toast("Failed to edit song in DB",Toast.LENGTH_SHORT);
                    }
                });
    }



    public void removeSongFromUserFavoritesInDB(String songID, int index) {
        DataManager.getInstance().getUser().getFavoriteSongs().getList().remove(index);

        usersRef.child(DataManager.getInstance().getUser().getUserID()).child(SONGS_FAVORITE_LIST_KEY)
                .setValue(DataManager.getInstance().getUser().getFavoriteSongs())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void changeSongLockMode(String songID, boolean isLock) {
        songsRef.child(songID).child("lock_mode").setValue(isLock);
    }

    public DisplayedSongChange_Callback getDisplayedSongChange_callback() {
        return displayedSongChange_callback;
    }

    public MyDB setDisplayedSongChange_callback(DisplayedSongChange_Callback displayedSongChange_callback) {
        this.displayedSongChange_callback = displayedSongChange_callback;
        return this;
    }
}
