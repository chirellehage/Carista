package com.carista.ui.main.fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carista.R;
import com.carista.data.realtimedb.models.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static androidx.core.content.ContextCompat.getDrawable;

public class UserPostsRecyclerViewAdapter extends RecyclerView.Adapter<UserPostsRecyclerViewAdapter.ViewHolder> {

    private final List<PostModel> items;
    AlertDialog.Builder alertTitle;
    public UserPostsRecyclerViewAdapter() {
        this.items = new ArrayList<>();
    }
    public void addPost(PostModel... postModel) {
        this.items.addAll(Arrays.asList(postModel));
        notifyDataSetChanged();
    }
    public void addPost(List<PostModel> postModels) {
        this.items.addAll(postModels);
        notifyDataSetChanged();
    }

    public void showQuickView(View v ,int position) {
        alertTitle = new AlertDialog.Builder(v.getContext());
        ImageView img = new ImageView(v.getContext());
        alertTitle.setTitle(this.items.get(position).title);
        alertTitle.setView(img);
        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        Picasso.get().load(items.get(position).image).placeholder(R.drawable.car_placeholder).into(img);
        layout.addView(img);
        alertTitle.setView(layout);
        alertTitle.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel(); // closes dialog
            }
        });
        alertTitle.show(); // display the dialog

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_posts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);
        holder.mTitleView.setText(this.items.get(position).title);
        Picasso.get().load(items.get(position).image).fit().centerCrop().into(holder.mImageView);
        holder.mImageView.setOnLongClickListener(v -> {
            showQuickView(v,position);
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public PostModel mItem;
        public final ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.post_title);
            mImageView = view.findViewById(R.id.post_image);
        }
    }
    public void clearData() {
        this.items.clear();
        notifyDataSetChanged();
    }
}