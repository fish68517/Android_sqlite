package com.example.healthdietapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.models.Post;
import com.example.healthdietapp.utils.AnimationUtils;

import java.util.List;

/**
 * PostAdapter - Adapter for displaying posts in RecyclerView
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public PostAdapter(List<Post> posts, OnPostClickListener listener) {
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, listener);
        
        // Apply list item enter animation with staggered delay
        AnimationUtils.applyListItemEnterAnimationWithDelay(holder.itemView, position * 50);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView postTitle;
        private TextView postContent;
        private TextView postLikes;
        private ImageView postImage;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
            postLikes = itemView.findViewById(R.id.postLikes);
            postImage = itemView.findViewById(R.id.postImage);
        }

        void bind(Post post, OnPostClickListener listener) {
            postTitle.setText(post.getTitle());
            postContent.setText(post.getContent());
            postLikes.setText(String.valueOf(post.getLikes()) + " likes");
            itemView.setOnClickListener(v -> listener.onPostClick(post));
        }
    }
}
