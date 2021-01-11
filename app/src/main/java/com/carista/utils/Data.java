package com.carista.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.carista.data.realtimedb.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Data {

    public static void uploadPost(String title, long id, String imageURL) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference post = mDatabase.child("/posts/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + id);

        post.setValue(new PostModel(title, imageURL));
    }

    public static void uploadAvatarLink(String avatarURL) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference user = mDatabase.child("/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/avatar");
        user.setValue(avatarURL);
    }

    public static void uploadNickname(String nickname) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference user = mDatabase.child("/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/nickname");
        user.setValue(nickname);
    }

    public static void removePost(String id) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Query post = mDatabase.child("/posts/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + id);

        post.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appleSnapshot : snapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Remove Post", "onCancelled", error.toException());
            }
        });
    }
}
