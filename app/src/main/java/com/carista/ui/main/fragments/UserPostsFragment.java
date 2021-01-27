package com.carista.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carista.R;
import com.carista.data.db.AppDatabase;
import com.carista.data.realtimedb.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserPostsFragment extends Fragment {
    private UserPostsRecyclerViewAdapter adapter;

    public UserPostsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_user_posts_list, container, false);
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        int mColumnCount = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        adapter = new UserPostsRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        return view ;
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userPostsRef = mDatabase.child("/posts/");

        userPostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                adapter.clearData();

                /*try {
                    Thread thread = new Thread(() -> AppDatabase.getInstance().postDao().deleteAll());
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    String id = post.getKey();
                    PostModel postModel = new PostModel(id, post.getValue());
                    adapter.addPost(postModel);
                    //AppDatabase.executeQuery(() -> AppDatabase.getInstance().postDao().insertAll(postModel));
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }
        });
    }
}