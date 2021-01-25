package com.carista.data.realtimedb.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.PropertyName;

import java.util.Date;
import java.util.HashMap;

@Entity
public class PostModel {
    @PrimaryKey
    @NonNull
    public String id = "";

    @ColumnInfo(name = "title")
    @PropertyName("title")
    public String title;

    @ColumnInfo(name = "image")
    @PropertyName("image")
    public String image;

    @ColumnInfo(name = "timestamp")
    @PropertyName("timestamp")
    public long timestamp;

    public PostModel(String title, String image) {
        this.title = title;
        this.image = image;
        this.timestamp = new Date().getTime();
    }

    public PostModel(String id, Object data) {
        HashMap<String, Object> _data = (HashMap<String, Object>) data;
        this.id = id;
        this.title = (String) _data.get("title");
        this.image = (String) _data.get("image");
        this.timestamp = (long) _data.get("timestamp");
    }
}