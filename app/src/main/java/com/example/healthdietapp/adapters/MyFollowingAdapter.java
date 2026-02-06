package com.example.healthdietapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.models.User;
import com.example.healthdietapp.utils.AnimationUtils;

import java.util.List;

/**
 * MyFollowingAdapter - Adapter for displaying followed authors in RecyclerView
 */
public class MyFollowingAdapter extends RecyclerView.Adapter<MyFollowingAdapter.FollowingViewHolder> {

    private List<User> followingUsers;
    private OnAuthorClickListener clickListener;
    private OnUnfollowClickListener unfollowListener;

    public interface OnAuthorClickListener {
        void onAuthorClick(User user);
    }

    public interface OnUnfollowClickListener {
        void onUnfollowClick(User user);
    }

    public MyFollowingAdapter(List<User> followingUsers, OnAuthorClickListener clickListener, 
                             OnUnfollowClickListener unfollowListener) {
        this.followingUsers = followingUsers;
        this.clickListener = clickListener;
        this.unfollowListener = unfollowListener;
    }

    @NonNull
    @Override
    public FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_following_author, parent, false);
        return new FollowingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingViewHolder holder, int position) {
        User user = followingUsers.get(position);
        holder.bind(user, clickListener, unfollowListener);
        
        // Apply list item enter animation with staggered delay
        AnimationUtils.applyListItemEnterAnimationWithDelay(holder.itemView, position * 50);
    }

    @Override
    public int getItemCount() {
        return followingUsers.size();
    }

    public void updateFollowingUsers(List<User> newUsers) {
        this.followingUsers = newUsers;
        notifyDataSetChanged();
    }

    static class FollowingViewHolder extends RecyclerView.ViewHolder {
        private ImageView authorAvatar;
        private TextView authorNickname;
        private TextView authorBio;
        private Button unfollowButton;

        FollowingViewHolder(@NonNull View itemView) {
            super(itemView);
            authorAvatar = itemView.findViewById(R.id.authorAvatar);
            authorNickname = itemView.findViewById(R.id.authorNickname);
            authorBio = itemView.findViewById(R.id.authorBio);
            unfollowButton = itemView.findViewById(R.id.unfollowButton);
        }

        void bind(User user, OnAuthorClickListener clickListener, OnUnfollowClickListener unfollowListener) {
            authorNickname.setText(user.getNickname() != null ? user.getNickname() : user.getUsername());
            authorBio.setText("Author");
            
            itemView.setOnClickListener(v -> clickListener.onAuthorClick(user));
            unfollowButton.setOnClickListener(v -> {
                AnimationUtils.applyRippleEffect(unfollowButton);
                unfollowListener.onUnfollowClick(user);
            });
        }
    }
}
