package com.carista.data.realtimedb.models;

import com.google.firebase.database.PropertyName;

import java.util.HashMap;


public class CommentModel {

    @PropertyName("comment")
    public String comment;

    @PropertyName("user")
    public String user;

    public CommentModel(String c, String u){
        this.comment=c;
        this.user=u;
    }

    public CommentModel(Object data) {
        HashMap<String, String> _data = (HashMap<String, String>) data;
        this.comment = _data.get("comment");
        this.user = _data.get("user");
    }
}
