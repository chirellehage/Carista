package com.carista.data.realtimedb.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.HashMap;

@Entity
public class PostModel {
    @PrimaryKey
    @NonNull
    @Expose
    public String id;

    @Expose
    @ColumnInfo(name = "title")
    @PropertyName("title")
    public String title;

    @Expose
    @ColumnInfo(name = "image")
    @PropertyName("image")
    public String image;

    @Expose
    @ColumnInfo(name = "timestamp")
    @PropertyName("timestamp")
    public long timestamp;

    @Expose
    @ColumnInfo(name = "userId")
    @PropertyName("userId")
    public String userId;

    @Exclude
    @ColumnInfo(name = "likes")
    public int likes;

    @Exclude
    @ColumnInfo(name = "username")
    public String username;

    @Exclude
    @ColumnInfo(name = "likedByUser")
    public boolean likedByUser;

    public PostModel(String title, String image, String userId) {
        this.title = title;
        this.image = image;
        this.timestamp = new Date().getTime();
        this.userId = userId;
    }

    public PostModel(String id, Object data) {
        HashMap<String, Object> _data = (HashMap<String, Object>) data;
        this.id = id;
        this.title = (String) _data.get("title");
        this.image = (String) _data.get("image");
        this.timestamp = (long) _data.get("timestamp");
        this.userId = (String) _data.get("userId");
        if (_data.get("likes") != null) {
            this.likes = ((HashMap) _data.get("likes")).size();
            for (Object like : ((HashMap) _data.get("likes")).values().toArray()) {
                if (like.toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    this.likedByUser = true;
                    break;
                }
            }
        }

    }
}