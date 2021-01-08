package com.carista;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.carista.data.db.AppDatabase;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase.initialize(getApplicationContext());

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.dark_theme_pref), Context.MODE_PRIVATE);
        int isDarkTheme=sharedPreferences.getInt(getString(R.string.dark_theme_enabled),1);
        if(isDarkTheme==1)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
