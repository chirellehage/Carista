package com.carista.utils;

import com.carista.data.realtimedb.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Data {

    public static void uploadPost(String title, long id, String imageURL) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference post = mDatabase.child("/posts/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + id);

        post.setValue(new PostModel(title, imageURL));
    }
}
