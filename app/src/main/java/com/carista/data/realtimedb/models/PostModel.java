package com.carista.data.realtimedb.models;

import com.google.firebase.database.PropertyName;

import java.util.HashMap;

public class PostModel {
    public String id;
    @PropertyName("title")
    public String title;
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
