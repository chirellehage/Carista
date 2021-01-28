package com.carista.data.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.carista.data.dao.PostDao;
import com.carista.data.realtimedb.models.PostModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PostModel.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    private static AppDatabase appDatabase;

    public static void initialize(Context applicationContext) {
        appDatabase = Room.databaseBuilder(applicationContext,
                AppDatabase.class, "caristav4.db")
                .build();
    }

    public abstract PostDao postDao();

    public static AppDatabase getInstance() {
        return appDatabase;
    }

    public static void executeQuery(Runnable query) {
        executor.execute(query);
    }

    public static void terminate() {
        executor.shutdown();
    }
}
