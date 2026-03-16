package com.hakimi.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hakimi.R;
import com.hakimi.model.Post;
import com.hakimi.network.ApiService;
import com.hakimi.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private List<Post> postList;
    private OnPostInteractionListener listener;

    public interface OnPostInteractionListener {
        void onLikeClick(Post post, int position);
        void onCommentClick(Post post, int position);
        void onPostClick(Post post);
    }

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    public void setOnPostInteractionListener(OnPostInteractionListener listener) {
        this.listener = listener;
    }

    public void setPosts(List<Post> newPosts) {
        this.postList = newPosts == null ? new ArrayList<>() : newPosts;
        notifyDataSetChanged();
    }

    public void updatePost(int position, Post post) {
        if (position >= 0 && position < postList.size()) {
            postList.set(position, post);
            notifyItemChanged(position);
        }
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

        holder.tvContent.setText(post.getContent());
        holder.tvTime.setText(formatTime(post.getCreatedAt()));
        holder.tvLikeCount.setText(String.valueOf(post.getLikesCount() == null ? 0 : post.getLikesCount()));
        holder.tvCommentCount.setText(String.valueOf(post.getComments() == null ? 0 : post.getComments().size()));
        holder.tvAuthorName.setText("用户#" + (post.getUserId() == null ? "-" : post.getUserId()));

        if (!TextUtils.isEmpty(post.getImagePath())) {
            holder.ivPostImage.setVisibility(View.VISIBLE);
            String imageUrl = ApiService.BASE_URL.replace("/api/", "") + trimLeadingSlash(post.getImagePath());
            ImageLoader.loadImage(imageUrl, holder.ivPostImage);
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }

        holder.llLikeBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLikeClick(post, position);
            }
        });
        holder.llCommentBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCommentClick(post, position);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    private String trimLeadingSlash(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private String formatTime(String createdAt) {
        if (TextUtils.isEmpty(createdAt)) {
            return "刚刚";
        }
        return createdAt.replace("T", " ");
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAuthorAvatar;
        TextView tvAuthorName;
        TextView tvTime;
        TextView tvContent;
        ImageView ivPostImage;
        LinearLayout llLikeBtn;
        TextView tvLikeCount;
        LinearLayout llCommentBtn;
        TextView tvCommentCount;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAuthorAvatar = itemView.findViewById(R.id.iv_author_avatar);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvTime = itemView.findViewById(R.id.tv_post_time);
            tvContent = itemView.findViewById(R.id.tv_post_content);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);
            llLikeBtn = itemView.findViewById(R.id.ll_like_btn);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            llCommentBtn = itemView.findViewById(R.id.ll_comment_btn);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
        }
    }
}
