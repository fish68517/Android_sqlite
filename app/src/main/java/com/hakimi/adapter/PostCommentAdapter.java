package com.hakimi.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hakimi.R;
import com.hakimi.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.CommentViewHolder> {

    private final List<Comment> commentList = new ArrayList<>();

    public void setComments(List<Comment> comments) {
        commentList.clear();
        if (comments != null) {
            commentList.addAll(comments);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvUser.setText("\u7528\u6237#" + (comment.getUserId() == null ? "-" : comment.getUserId()));
        holder.tvTime.setText(formatTime(comment.getCreatedAt()));
        holder.tvContent.setText(comment.getContent() == null ? "" : comment.getContent());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private String formatTime(String createdAt) {
        if (createdAt == null || createdAt.trim().isEmpty()) {
            return "\u521a\u521a";
        }
        return createdAt.replace("T", " ");
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUser;
        private final TextView tvTime;
        private final TextView tvContent;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tv_comment_user);
            tvTime = itemView.findViewById(R.id.tv_comment_time);
            tvContent = itemView.findViewById(R.id.tv_comment_content);
        }
    }
}
