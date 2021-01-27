package com.carista.utils;

import android.text.Html;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.carista.data.realtimedb.models.CommentModel;
import com.carista.data.realtimedb.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Data {

    public static void uploadPost(String title, long id, String imageURL, String userId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference post = mDatabase.child("/posts/" + id);

        post.setValue(new PostModel(title, imageURL, userId));
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

    public static void addLike(String postId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference posts = databaseReference.child("/posts");
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postIds : snapshot.getChildren()) {
                    if (postIds.getKey().equals(postId)) {
                        DatabaseReference postLikes = posts.child(postId);
                        postLikes.child("likes").push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void addComment(String postId, CommentModel commentModel) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference posts = databaseReference.child("/posts");
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postIds : snapshot.getChildren()) {
                    if (postIds.getKey().equals(postId)) {
                        DatabaseReference postLikes = posts.child(postId);
                        postLikes.child("comments").push().setValue(commentModel);
                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getLikesCount(String postId, TextView view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference posts = databaseReference.child("/posts");
        posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postIds : snapshot.getChildren()) {
                    if (postIds.getKey().equals(postId)) {
                        long count = postIds.child("likes").getChildrenCount();
                        if (count == 1)
                            view.setText(count + " like");
                        else
                            view.setText(count + " likes");
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void setCommentAvatarNickname(CommentModel commentModel, CircleImageView userAvatar, TextView userNicknameText) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mDatabase.child("/users/" + commentModel.user);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot avatar = dataSnapshot.child("avatar");
                DataSnapshot nickname = dataSnapshot.child("nickname");
                String nicknameText = null;
                if (nickname == null || nickname.getValue() == null || nickname.getValue().toString().isEmpty())
                    nicknameText = "<b>Anonymous</b> " + commentModel.comment;
                else
                    nicknameText = "<b>" + nickname.getValue().toString() + "</b> " + commentModel.comment;
                userNicknameText.setText(Html.fromHtml(nicknameText));

                if (avatar.getValue() != null)
                    Picasso.get().load(avatar.getValue().toString()).into(userAvatar);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }
        });
    }

    public static void setPostNicknameTitle(String user, String title, TextView userNicknameTitle) {
        if (user.equals("Unknown"))
            return;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mDatabase.child("/users/" + user);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nickname = dataSnapshot.child("nickname");
                String nicknameText = null;
                if (nickname == null || nickname.getValue() == null || nickname.getValue().toString().isEmpty())
                    nicknameText = "<b>Anonymous</b> " + title;
                else
                    nicknameText = "<b>" + nickname.getValue().toString() + "</b> " + title;
                userNicknameTitle.setText(Html.fromHtml(nicknameText));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }
        });
    }

    public static void isLikedByUser(String postId, CheckBox view, TextView view1) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference posts = databaseReference.child("/posts");
        posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postIds : snapshot.getChildren()) {
                    if (postIds.getKey().equals(postId)) {
                        for (DataSnapshot likes : postIds.child("likes").getChildren()) {
                            if (likes.getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                view.setChecked(true);
                                int likesNb = Integer.parseInt(view1.getText().toString().split(" ")[0]);
                                if (likesNb > 1)
                                    view1.setText("You and " + (likesNb - 1) + " others like this");
                                else view1.setText("Only you like this");
                                return;
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

    public static void removeLike(String postId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference posts = databaseReference.child("/posts");
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postIds : snapshot.getChildren()) {
                    if (postIds.getKey().equals(postId)) {
                        for (DataSnapshot likes : postIds.child("likes").getChildren()) {
                            if (likes.getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                likes.getRef().setValue(null);
                                return;
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
        Query post = mDatabase.child("/posts" + id);

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
