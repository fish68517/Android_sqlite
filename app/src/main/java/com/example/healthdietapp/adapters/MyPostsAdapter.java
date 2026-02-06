package com.example.healthdietapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.models.Post;
import com.example.healthdietapp.utils.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * MyPostsAdapter - Adapter for displaying user's posts with delete functionality
 */
public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.PostViewHolder> {

    private Context context;
    private List<Post> posts;
    private OnPostClickListener clickListener;
    private OnPostLongClickListener longClickListener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public interface OnPostLongClickListener {
        void onPostLongClick(Post post);
    }

    public MyPostsAdapter(Context context, OnPostClickListener clickListener, OnPostLongClickListener longClickListener) {
        this.context = context;
        this.posts = new ArrayList<>();
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, clickListener, longClickListener);
        
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
        private TextView postDate;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
            postLikes = itemView.findViewById(R.id.postLikes);
            postDate = itemView.findViewById(R.id.postDate);
        }

        void bind(Post post, OnPostClickListener clickListener, OnPostLongClickListener longClickListener) {
            postTitle.setText(post.getTitle());
            postContent.setText(post.getContent());
            postLikes.setText(post.getLikes() + " likes");
            
            // Format date
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            postDate.setText(sdf.format(new java.util.Date(post.getCreatedAt())));
            
            itemView.setOnClickListener(v -> clickListener.onPostClick(post));
            itemView.setOnLongClickListener(v -> {
                longClickListener.onPostLongClick(post);
                return true;
            });
        }
    }
}
