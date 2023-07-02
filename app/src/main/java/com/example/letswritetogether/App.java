package com.example.letswritetogether;

import android.app.Application;

import com.example.letswritetogether.Utilities.SignalGenerator;
import com.google.firebase.FirebaseApp;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        SignalGenerator.init(this);
    }
}
