package com.carista;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.carista.api.RetrofitManager;
import com.carista.data.db.AppDatabase;

public class App extends Application {

    public static final String PREF_DARK_THEME = "dark_theme";

    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase.initialize(getApplicationContext());

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PREF_DARK_THEME, Context.MODE_PRIVATE);

        boolean isDarkTheme = sharedPreferences.getBoolean(PREF_DARK_THEME, false);

        if (isDarkTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        RetrofitManager.getInstance(getApplicationContext());
    }
}
