package com.example.xiaoshuo.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xiaoshuo.R;
import com.example.xiaoshuo.models.Comment;
import com.example.xiaoshuo.utils.CommunityDataManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private OnCommentClickListener listener;

    public interface OnCommentClickListener {
        void onCommentClick(Comment comment);
        void onLikeClick(Comment comment, int position);
        void onReplyClick(Comment comment);
    }

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    public void setOnCommentClickListener(OnCommentClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Comment> newComments) {
        this.commentList = newComments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        
        // 设置作者信息
        holder.tvAuthorName.setText(comment.getAuthorName());
        if (comment.getAuthorAvatar() != null && !comment.getAuthorAvatar().isEmpty()) {
            Glide.with(context)
                .load(comment.getAuthorAvatar())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.ivAuthorAvatar);
        }
        
        // 设置评论内容
        if (comment.isReply() && comment.getReplyToUserName() != null) {
            // 如果是回复评论，显示回复对象
            SpannableString spannableString = new SpannableString("回复 @" + comment.getReplyToUserName() + ": " + comment.getContent());
            spannableString.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary)),
                    3,
                    5 + comment.getReplyToUserName().length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            holder.tvCommentContent.setText(spannableString);
        } else {
            holder.tvCommentContent.setText(comment.getContent());
        }
        
        // 设置发布时间
        holder.tvPublishTime.setText(CommunityDataManager.formatTimeAgo(comment.getPublishTime()));
        
        // 设置点赞数
        holder.tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
        
        // 设置点赞状态
        if (comment.isLiked()) {
            holder.ivLike.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.ivLike.setImageResource(android.R.drawable.btn_star_big_off);
        }
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCommentClick(comment);
            }
        });
        
        holder.layoutLike.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLikeClick(comment, position);
            }
        });
        
        holder.layoutReply.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReplyClick(comment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAuthorAvatar;
        TextView tvAuthorName;
        TextView tvPublishTime;
        TextView tvCommentContent;
        ImageView ivLike;
        TextView tvLikeCount;
        View layoutLike;
        View layoutReply;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAuthorAvatar = itemView.findViewById(R.id.iv_comment_avatar);
            tvAuthorName = itemView.findViewById(R.id.tv_comment_author);
            tvPublishTime = itemView.findViewById(R.id.tv_comment_time);
            tvCommentContent = itemView.findViewById(R.id.tv_comment_content);
            ivLike = itemView.findViewById(R.id.iv_comment_like);
            tvLikeCount = itemView.findViewById(R.id.tv_comment_like_count);
            layoutLike = itemView.findViewById(R.id.layout_comment_like);
            layoutReply = itemView.findViewById(R.id.layout_comment_reply);
        }
    }
} 