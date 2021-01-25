package com.carista.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import com.carista.R;
import com.carista.data.realtimedb.models.CommentModel;
import com.carista.utils.Data;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CommentsActivity extends AppCompatActivity {

    private CircleImageView myCommentAvatar;
    private EditText myComment;
    private CheckBox postComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.comments_title);

        myCommentAvatar=findViewById(R.id.my_comment_user_avatar);
        myComment=findViewById(R.id.my_comment);
        postComment=findViewById(R.id.comment_post_button);

        String postId=getIntent().getExtras().getString("postId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mDatabase.child("/users/" + user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot avatar = dataSnapshot.child("avatar");
                if (avatar.getValue() != null)
                    Picasso.get().load(avatar.getValue().toString()).into(myCommentAvatar);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }
        });



        postComment.setOnClickListener(view -> {
            String comment=myComment.getText().toString();
            if(comment.isEmpty())
                return;
            CommentModel commentModel=new CommentModel(comment, FirebaseAuth.getInstance().getCurrentUser().getUid());
            Data.addComment(postId,commentModel);
            myComment.setText("");
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}