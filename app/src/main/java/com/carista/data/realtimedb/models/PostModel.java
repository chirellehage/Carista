package com.carista.data.realtimedb.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.PropertyName;

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

    public PostModel(String title, String image) {
        this.title = title;
        this.image = image;
    }

    public PostModel(String id, Object data) {
        HashMap<String, String> _data = (HashMap<String, String>) data;
        this.id = id;
        this.title = _data.get("title");
        this.image = _data.get("image");
    }
}
