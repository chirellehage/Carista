package com.carista.ui.main.fragments;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carista.R;
import com.carista.data.realtimedb.models.CommentModel;
import com.carista.utils.Data;

import java.util.ArrayList;
import java.util.List;


public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder> {

    private final List<CommentModel> items;

    public CommentsRecyclerViewAdapter() {
        this.items=new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_comments_item, parent, false);
        return new ViewHolder(view);
    }

    public void addComment(CommentModel commentModel){
        this.items.add(commentModel);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);
        Data.setCommentAvatarNickname(holder.mItem, holder.commentAvatar, holder.commentNicknameText);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public CircleImageView commentAvatar;
        public TextView commentNicknameText;
        public CommentModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            commentAvatar=view.findViewById(R.id.comment_user_avatar);
            commentNicknameText=view.findViewById(R.id.comment_nickname_text);
        }

    }

    public void clearData() {
        this.items.clear();
        notifyDataSetChanged();
    }
}