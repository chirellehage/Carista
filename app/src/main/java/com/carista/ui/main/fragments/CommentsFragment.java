package com.carista.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.carista.R;
import com.carista.data.realtimedb.models.CommentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class CommentsFragment extends Fragment {

    private CommentsRecyclerViewAdapter adapter;


    public CommentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments_item_list, container, false);
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.comments_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommentsRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String postId=getActivity().getIntent().getExtras().getString("postId");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference postRef=mDatabase.child("posts");
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clearData();
                for(DataSnapshot postIds: snapshot.getChildren()){
                    if(postIds.getKey().equals(postId)){
                        if(postIds.child("comments")==null)
                            return;
                        for(DataSnapshot comment: postIds.child("comments").getChildren()){
                            adapter.addComment(new CommentModel(comment.getValue()));
                        }
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}