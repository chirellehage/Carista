package com.carista;

import android.app.Application;

import com.carista.data.db.AppDatabase;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase.initialize(getApplicationContext());
    }
}
