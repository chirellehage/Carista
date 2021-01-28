package com.carista.utils;

import android.text.Html;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.carista.data.realtimedb.models.CommentModel;
import com.carista.data.realtimedb.models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Data {

    public static void uploadPost(String title, long id, String imageURL, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .add(new PostModel(title, imageURL, userId));
    }

    public static void uploadAvatarLink(String avatarURL) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("avatar",avatarURL);
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(data, SetOptions.merge());
    }

    public static void uploadNickname(String nickname) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("nickname",nickname);
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(data, SetOptions.merge());
    }

    public static void addLike(String postId) {
        ArrayList<String> likesList = new ArrayList<String>();
        likesList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Map<String, Object> data = new HashMap<>();
        data.put("LikesArray",likesList);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postId).set(data,SetOptions.merge());

    }

    public static void addComment(String postId, CommentModel commentModel) {
        ArrayList<CommentModel> CommentsList = new ArrayList<CommentModel>();
        CommentsList.add(commentModel);
        Map<String, Object> data = new HashMap<>();
        data.put("Comments",CommentsList);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postId).update("Comments", FieldValue.arrayUnion(commentModel));
    }

    public static void getLikesCount(String postId, TextView view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("posts").document(postId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)
                    return;
                if(value != null && value.exists()){
                    ArrayList<String> likesList = (ArrayList<String>)value.get("LikesArray");
                    if(likesList != null){
                        int count = likesList.size();
                        if(likesList.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            if (count == 1)
                                view.setText(count + " like");
                            else
                                view.setText(count + " likes");
                            return;
                        }else {
                            view.setText(count + " likes");
                            return;
                        }
                    }
                }
            }

        });
    }

    public static void setCommentAvatarNickname(CommentModel commentModel, CircleImageView userAvatar, TextView userNicknameText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(commentModel.user)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                            return;
                        String avatar = (String)value.get("avatar");
                        String nickname = (String)value.get("nickname");
                        String nicknameText = null;
                        if (nickname == null  || nickname.isEmpty())
                            nicknameText = "<b>Anonymous</b> " + commentModel.comment;
                        else
                            nicknameText = "<b>" + nickname+ "</b> " + commentModel.comment;
                        userNicknameText.setText(Html.fromHtml(nicknameText));

                        if (avatar != null)
                            Picasso.get().load(avatar.toString()).into(userAvatar);
                    }
                });
    }

    public static void setPostNicknameTitle(String user, String title, TextView userNicknameTitle) {
        if (user.equals("Unknown"))
            return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                            return;
                        String nickname = (String)value.get("nickname");
                        String nicknameText = null;
                        if (nickname == null  || nickname.isEmpty())
                            nicknameText = "<b>Anonymous</b> " + title;
                        else
                            nicknameText = "<b>" + nickname.toString() + "</b> " + title;
                        userNicknameTitle.setText(Html.fromHtml(nicknameText));
                    }
                });
    }

    public static void isLikedByUser(String postId, CheckBox view, TextView view1) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("posts").document(postId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)
                    return;
                if(value != null && value.exists()){
                    if(!value.contains("LikesArray")){
                        view1.setText("Press Like button to support");
                    }
                    ArrayList<String> likesList = (ArrayList<String>)value.get("LikesArray");
                    if(likesList != null ){
                        if(likesList.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            view.setChecked(true);
                            int likesNb = Integer.parseInt(view1.getText().toString().split(" ")[0]);
                            if (likesNb > 1)
                                view1.setText("You and " + (likesNb - 1) + " others like this");
                            else view1.setText("Only you like this");
                            return;
                        }
                        else
                        {
                            view1.setText("Press Like button to support");
                        }
                    }
                }

            }
        });
    }

    public static void removeLike(String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postId).update("LikesArray",FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()));
    }

    public static void removePost(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(id).delete().addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Remove Post", "onCancelled", e);
            }
        });
    }
}
