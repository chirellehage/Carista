package com.carista.utils;

import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

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

    public static void addLike(String postId){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        DatabaseReference posts=databaseReference.child("/posts");
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userPosts: snapshot.getChildren()){
                    for(DataSnapshot postIds: userPosts.getChildren()){
                        if(postIds.getKey().equals(postId)){
                            DatabaseReference postLikes=posts.child(userPosts.getKey().toString()).child(postId);
                            postLikes.child("likes").push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getLikesCount(String postId, TextView view){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        DatabaseReference posts=databaseReference.child("/posts");
        posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userPosts: snapshot.getChildren()){
                    for(DataSnapshot postIds: userPosts.getChildren()){
                        if(postIds.getKey().equals(postId)){
                            long count = postIds.child("likes").getChildrenCount();
                            if(count==1)
                                view.setText(count+" like");
                            else
                                view.setText(count+" likes");
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void isLikedByUser(String postId, CheckBox view, TextView view1){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        DatabaseReference posts=databaseReference.child("/posts");
        posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userPosts: snapshot.getChildren()){
                    for(DataSnapshot postIds: userPosts.getChildren()){
                        if(postIds.getKey().equals(postId)){
                            for(DataSnapshot likes: postIds.child("likes").getChildren()){
                                if(likes.getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    view.setChecked(true);
                                    view1.setText(view1.getText().toString()+" including you");
                                    return;
                                }
                            }
                        }
                    }
                }
                view.setChecked(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void removeLike(String postId){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        DatabaseReference posts=databaseReference.child("/posts");
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userPosts: snapshot.getChildren()){
                    for(DataSnapshot postIds: userPosts.getChildren()){
                        if(postIds.getKey().equals(postId)){
                            for(DataSnapshot likes: postIds.child("likes").getChildren()){
                                if(likes.getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    likes.getRef().setValue(null);
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
