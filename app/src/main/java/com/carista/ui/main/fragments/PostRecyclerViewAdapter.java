package com.carista.ui.main.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.carista.R;
import com.carista.data.realtimedb.models.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder> {

    private final List<PostModel> items;

    public PostRecyclerViewAdapter() {
        this.items = new ArrayList<>();
    }

    public void addPost(PostModel postModel) {
        this.items.add(postModel);
        notifyItemChanged(this.items.size() + 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);
        holder.mTitleView.setText(this.items.get(position).title);
        Picasso.get().load(this.items.get(position).image).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final ImageView mImageView;
        public PostModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.post_title);
            mImageView = view.findViewById(R.id.post_image);
        }
    }

    public void clearData() {
        this.items.clear();
    }
}
