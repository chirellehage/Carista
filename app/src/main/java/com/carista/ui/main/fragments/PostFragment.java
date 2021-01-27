package com.carista.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;

public class PostFragment extends Fragment {

    private PostRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private String lastLazyItem;
    private String lastLazyKey;

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
        if (Device.isNetworkAvailable(getContext()))
            databaseReference.orderByKey().limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Thread thread = new Thread(() -> AppDatabase.getInstance().postDao().deleteAll());
                        thread.start();
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ArrayList<PostModel> postModels = new ArrayList<>();
                    ArrayList<String> postKeys = new ArrayList<>();

                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                        String id = post.getKey();
                        PostModel postModel = new PostModel(id, post.getValue());
                        postModels.add(postModel);
                        postKeys.add(id);
                    }

                    for (int i = postModels.size() - 1; i >= 0; i--) {
                        adapter.addPost(postModels.get(i));
                        final int j = i;
                        AppDatabase.executeQuery(() -> AppDatabase.getInstance().postDao().insertAll(postModels.get(j)));
                        lastLazyItem = postKeys.get(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("ERROR", "Failed to read value.", error.toException());
                }
            });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!Device.isNetworkAvailable(getContext()))
                    return;
                if (!recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)) {
                    if (lastLazyItem != null) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = database.getReference("posts");
                        databaseReference.orderByKey().endAt(lastLazyItem).limitToLast(6).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Thread thread = new Thread(() -> AppDatabase.getInstance().postDao().deleteAll());
                                    thread.start();
                                    thread.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                ArrayList<PostModel> postModels = new ArrayList<>();
                                ArrayList<String> postKeys = new ArrayList<>();

                                for (DataSnapshot post : dataSnapshot.getChildren()) {
                                    String id = post.getKey();
                                    PostModel postModel = new PostModel(id, post.getValue());
                                    postModels.add(postModel);
                                    postKeys.add(id);
                                }

                                for (int i = postModels.size() - 2; i >= 0; i--) {
                                    adapter.addPost(postModels.get(i));
                                    final int j = i;
                                    AppDatabase.executeQuery(() -> AppDatabase.getInstance().postDao().insertAll(postModels.get(j)));
                                    lastLazyItem = postKeys.get(i);
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
            }
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
