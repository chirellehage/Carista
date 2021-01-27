package com.carista.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carista.R;
import com.carista.data.db.AppDatabase;
import com.carista.data.realtimedb.models.PostModel;
import com.carista.utils.Device;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class PostFragment extends Fragment {

    private PostRecyclerViewAdapter adapter;
    private  RecyclerView recyclerView;
    private long oldesttimestamp;
    public PostFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new PostRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("posts");
        databaseReference.orderByChild("timestamp").limitToFirst(2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clearData();
                try {
                    Thread thread = new Thread(() -> AppDatabase.getInstance().postDao().deleteAll());
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (DataSnapshot post : dataSnapshot.getChildren()) {
//                    for (DataSnapshot post : user.getChildren()) {
                        String id = post.getKey();
                        PostModel postModel = new PostModel(id, post.getValue());
                        oldesttimestamp = postModel.timestamp;
                        adapter.addPost(postModel.userId,postModel);
                        AppDatabase.executeQuery(() -> AppDatabase.getInstance().postDao().insertAll(postModel));
//                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                this.currentScrollState = newState;
                this.isScrollCompleted();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }


            private void isScrollCompleted () {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {
                    /** To do code here*/
                    databaseReference.orderByChild("timestamp").startAt(oldesttimestamp).limitToFirst(2).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot post : dataSnapshot.getChildren()) {
                                String id = post.getKey();
                                PostModel postModel = new PostModel(id, post.getValue());
                                if(postModel.timestamp == oldesttimestamp)
                                    continue;
                                oldesttimestamp = postModel.timestamp;
                                adapter.addPost(postModel.userId,postModel);
                                AppDatabase.executeQuery(() -> AppDatabase.getInstance().postDao().insertAll(postModel));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            };
        });

        if (!Device.isNetworkAvailable(getContext())) {
            adapter.clearData();
            AppDatabase.executeQuery(() -> adapter.addPost(AppDatabase.getInstance().postDao().getAll()));
            Snackbar.make(getView().findViewById(R.id.list),
                    R.string.network_unavailable,
                    Snackbar.LENGTH_SHORT).show();
        }
    }
}
