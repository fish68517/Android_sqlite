package com.example.healthdietapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.models.Post;
import com.example.healthdietapp.utils.AnimationUtils;
import com.example.healthdietapp.utils.SessionManager;

/**
 * PostDetailActivity - Displays detailed view of a post with interaction options
 */
public class PostDetailActivity extends AppCompatActivity {

    private ImageView postImage;
    private TextView postTitle;
    private TextView postContent;
    private TextView postAuthor;
    private TextView postLikes;
    private Button likeButton;
    private Button collectButton;
    private Button followButton;

    private DatabaseHelper dbHelper;
    private PostDAO postDAO;
    private SessionManager sessionManager;
    private String userId;
    private String postId;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        initializeViews();
        initializeDatabase();
        loadPostData();
        setupInteractionButtons();
    }

    private void initializeViews() {
        postImage = findViewById(R.id.postDetailImage);
        postTitle = findViewById(R.id.postDetailTitle);
        postContent = findViewById(R.id.postDetailContent);
        postAuthor = findViewById(R.id.postDetailAuthor);
        postLikes = findViewById(R.id.postDetailLikes);
        likeButton = findViewById(R.id.likeButton);
        collectButton = findViewById(R.id.collectButton);
        followButton = findViewById(R.id.followButton);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        postDAO = new PostDAO(dbHelper);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void loadPostData() {
        postId = getIntent().getStringExtra("postId");
        if (postId == null) {
            Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            post = postDAO.getPostById(postId);
            runOnUiThread(() -> {
                if (post != null) {
                    displayPostData();
                    updateInteractionButtonStates();
                } else {
                    Toast.makeText(PostDetailActivity.this, "Failed to load post", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void displayPostData() {
        postTitle.setText(post.getTitle());
        postContent.setText(post.getContent());
        postAuthor.setText("By: " + post.getUserId());
        postLikes.setText(post.getLikes() + " likes");
        
        // Apply fade in animation to content
        AnimationUtils.applyFadeInAnimation(postTitle);
        AnimationUtils.applyFadeInAnimation(postContent);
    }

    private void setupInteractionButtons() {
        likeButton.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(likeButton);
            toggleLike();
        });
        collectButton.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(collectButton);
            toggleCollect();
        });
        followButton.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(followButton);
            toggleFollow();
        });
    }

    private void toggleLike() {
        new Thread(() -> {
            boolean hasLiked = postDAO.hasUserLikedPost(userId, postId);
            boolean success;

            if (hasLiked) {
                success = postDAO.unlikePost(userId, postId);
            } else {
                success = postDAO.likePost(userId, postId);
            }

            if (success) {
                post = postDAO.getPostById(postId);
                runOnUiThread(() -> {
                    displayPostData();
                    updateInteractionButtonStates();
                    String message = hasLiked ? "Unliked" : "Liked";
                    Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void toggleCollect() {
        new Thread(() -> {
            boolean hasCollected = postDAO.hasUserCollectedPost(userId, postId);
            boolean success;

            if (hasCollected) {
                success = postDAO.removeCollection(userId, postId);
            } else {
                success = postDAO.collectPost(userId, postId);
            }

            if (success) {
                runOnUiThread(() -> {
                    updateInteractionButtonStates();
                    String message = hasCollected ? "Removed from collection" : "Added to collection";
                    Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void toggleFollow() {
        new Thread(() -> {
            boolean isFollowing = postDAO.isUserFollowing(userId, post.getUserId());
            boolean success;

            if (isFollowing) {
                success = postDAO.unfollowUser(userId, post.getUserId());
            } else {
                success = postDAO.followUser(userId, post.getUserId());
            }

            if (success) {
                runOnUiThread(() -> {
                    updateInteractionButtonStates();
                    String message = isFollowing ? "Unfollowed" : "Followed";
                    Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void updateInteractionButtonStates() {
        new Thread(() -> {
            boolean hasLiked = postDAO.hasUserLikedPost(userId, postId);
            boolean hasCollected = postDAO.hasUserCollectedPost(userId, postId);
            boolean isFollowing = postDAO.isUserFollowing(userId, post.getUserId());

            runOnUiThread(() -> {
                likeButton.setText(hasLiked ? "Unlike" : "Like");
                collectButton.setText(hasCollected ? "Remove Collection" : "Collect");
                followButton.setText(isFollowing ? "Unfollow" : "Follow");
            });
        }).start();
    }
}
