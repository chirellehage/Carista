package com.carista.data.realtimedb.models;

import com.google.firebase.database.PropertyName;

public class UserModel {
    @PropertyName("nickname")
    public String nickname;

    @PropertyName("avatar")
    public String avatar;

    public UserModel(String nickname, String avatar) {
        this.nickname = nickname;
        this.avatar = avatar;
    }
}
