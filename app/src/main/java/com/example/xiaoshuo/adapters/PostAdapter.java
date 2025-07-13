package com.example.xiaoshuo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xiaoshuo.R;
import com.example.xiaoshuo.activities.PostDetailActivity;
import com.example.xiaoshuo.models.Post;
import com.example.xiaoshuo.utils.CommunityDataManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
        void onLikeClick(Post post, int position);
        void onCommentClick(Post post);
        void onShareClick(Post post);
    }

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    public void setOnPostClickListener(OnPostClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Post> newPosts) {
        this.postList = newPosts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        
        // 设置帖子类型标签
        holder.tvPostType.setText(post.getPostType());
        
        // 设置作者信息
        holder.tvAuthorName.setText(post.getAuthorName());
        if (post.getAuthorAvatar() != null && !post.getAuthorAvatar().isEmpty()) {
            Glide.with(context)
                .load(post.getAuthorAvatar())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.ivAuthorAvatar);
        }
        
        // 设置帖子内容
        holder.tvPostTitle.setText(post.getTitle());
        holder.tvPostContent.setText(post.getContent());
        
        // 设置发布时间
        holder.tvPublishTime.setText(CommunityDataManager.formatTimeAgo(post.getPublishTime()));
        
        // 设置互动数据
        holder.tvLikeCount.setText(String.valueOf(post.getLikeCount()));
        holder.tvCommentCount.setText(String.valueOf(post.getCommentCount()));
        
        // 设置点赞状态
        if (post.isLiked()) {
            holder.ivLike.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.ivLike.setImageResource(android.R.drawable.btn_star_big_off);
        }
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClick(post);
            } else {
                // 默认跳转到帖子详情页
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("post_id", post.getId());
                context.startActivity(intent);
            }
        });
        
        holder.layoutLike.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLikeClick(post, position);
            }
        });
        
        holder.layoutComment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCommentClick(post);
            }
        });
        
        holder.layoutShare.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShareClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAuthorAvatar;
        TextView tvAuthorName;
        TextView tvPublishTime;
        TextView tvPostType;
        TextView tvPostTitle;
        TextView tvPostContent;
        ImageView ivLike;
        TextView tvLikeCount;
        TextView tvCommentCount;
        View layoutLike;
        View layoutComment;
        View layoutShare;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAuthorAvatar = itemView.findViewById(R.id.iv_author_avatar);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvPublishTime = itemView.findViewById(R.id.tv_publish_time);
            tvPostType = itemView.findViewById(R.id.tv_post_type);
            tvPostTitle = itemView.findViewById(R.id.tv_post_title);
            tvPostContent = itemView.findViewById(R.id.tv_post_content);
            ivLike = itemView.findViewById(R.id.iv_like);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
            layoutLike = itemView.findViewById(R.id.layout_like);
            layoutComment = itemView.findViewById(R.id.layout_comment);
            layoutShare = itemView.findViewById(R.id.layout_share);
        }
    }
} 