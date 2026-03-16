package com.hakimi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hakimi.R;
import com.hakimi.model.Comment;
import com.hakimi.utils.ImageLoader;

import java.util.List;

/**
 * 评论适配器
 * 
 * @author hakimi
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> commentList;
    private android.content.Context context;

    public CommentAdapter(android.content.Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvContent.setText(comment.getContent());
        if (comment.getCreatedAt() != null) {
            holder.tvTime.setText(comment.getCreatedAt());
        } else {
            holder.tvTime.setText("");
        }
        
     /*   if (comment.getUser() != null) {
            holder.tvUsername.setText(comment.getUser().getUsername());
            if (comment.getUser().getAvatar() != null) {
                //ImageLoader.loadCircleImage(comment.getUser().getAvatar(), holder.ivAvatar);
            }
        }*/
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername;
        TextView tvTime;
        TextView tvContent;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_comment_avatar);
            tvUsername = itemView.findViewById(R.id.tv_comment_username);
            tvTime = itemView.findViewById(R.id.tv_comment_time);
            tvContent = itemView.findViewById(R.id.tv_comment_content);
        }
    }
}