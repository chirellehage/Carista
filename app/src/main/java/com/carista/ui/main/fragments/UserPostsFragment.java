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
import com.carista.utils.Device;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference query = db.collection("posts");
        if (Device.isNetworkAvailable(getContext())) {

            query.whereEqualTo("userId",FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w("LIST_USER_POSTS", "listen:error", error);
                        return;
                    }
                    adapter.clearData();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        String id = doc.getId();
                        PostModel postModel = new PostModel(id, doc.getData());
                        adapter.addPost(postModel);
                    }


                }
            });
        }
        if (!Device.isNetworkAvailable(getContext())) {
            adapter.clearData();
            AppDatabase.executeQuery(() -> adapter.addPost(AppDatabase.getInstance().postDao().getAll()));
            Snackbar.make(getView().findViewById(R.id.list),
                    R.string.network_unavailable,
                    Snackbar.LENGTH_SHORT).show();
        }
    }
}