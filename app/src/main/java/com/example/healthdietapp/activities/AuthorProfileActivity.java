package com.example.healthdietapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.adapters.PostAdapter;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.database.UserDAO;
import com.example.healthdietapp.models.Post;
import com.example.healthdietapp.models.User;
import com.example.healthdietapp.utils.SessionManager;

import java.util.List;

/**
 * AuthorProfileActivity - Displays author's profile and their posts
 */
public class AuthorProfileActivity extends AppCompatActivity {

    private ImageView authorAvatar;
    private TextView authorNickname;
    private TextView authorUsername;
    private Button followButton;
    private RecyclerView postsRecyclerView;
    private PostAdapter adapter;
    
    private DatabaseHelper dbHelper;
    private PostDAO postDAO;
    private UserDAO userDAO;
    private SessionManager sessionManager;
    private String currentUserId;
    private String authorId;
    private User author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_profile);

        initializeViews();
        initializeDatabase();
        setupBackButton();
        
        // Get author ID from intent
        authorId = getIntent().getStringExtra("userId");
        if (authorId != null) {
            loadAuthorProfile();
            loadAuthorPosts();
        }
    }

    private void initializeViews() {
        authorAvatar = findViewById(R.id.authorAvatar);
        authorNickname = findViewById(R.id.authorNickname);
        authorUsername = findViewById(R.id.authorUsername);
        followButton = findViewById(R.id.followButton);
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(java.util.Collections.emptyList(), this::onPostClick);
        postsRecyclerView.setAdapter(adapter);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        postDAO = new PostDAO(dbHelper);
        userDAO = new UserDAO(dbHelper);
        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();
    }

    private void setupBackButton() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void loadAuthorProfile() {
        new Thread(() -> {
            try {
                author = userDAO.getUserById(authorId);
                runOnUiThread(() -> {
                    if (author != null) {
                        authorNickname.setText(author.getNickname() != null ? author.getNickname() : author.getUsername());
                        authorUsername.setText("@" + author.getUsername());
                        
                        // Setup follow button
                        boolean isFollowing = postDAO.isUserFollowing(currentUserId, authorId);
                        updateFollowButton(isFollowing);
                        
                        followButton.setOnClickListener(v -> {
                            if (isFollowing) {
                                unfollowAuthor();
                            } else {
                                followAuthor();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadAuthorPosts() {
        new Thread(() -> {
            try {
                List<Post> posts = postDAO.getPostsByUser(authorId);
                runOnUiThread(() -> {
                    if (posts != null && !posts.isEmpty()) {
                        adapter.updatePosts(posts);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void followAuthor() {
        new Thread(() -> {
            try {
                boolean success = postDAO.followUser(currentUserId, authorId);
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(AuthorProfileActivity.this, "Following", Toast.LENGTH_SHORT).show();
                        updateFollowButton(true);
                    } else {
                        Toast.makeText(AuthorProfileActivity.this, "Failed to follow", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void unfollowAuthor() {
        new Thread(() -> {
            try {
                boolean success = postDAO.unfollowUser(currentUserId, authorId);
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(AuthorProfileActivity.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                        updateFollowButton(false);
                    } else {
                        Toast.makeText(AuthorProfileActivity.this, "Failed to unfollow", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateFollowButton(boolean isFollowing) {
        if (isFollowing) {
            followButton.setText(R.string.unfollow);
            followButton.setTextColor(getResources().getColor(R.color.md_theme_light_error, null));
        } else {
            followButton.setText(R.string.follow);
            followButton.setTextColor(getResources().getColor(R.color.md_theme_light_primary, null));
        }
    }

    private void onPostClick(Post post) {
        // Navigate to post detail
        android.content.Intent intent = new android.content.Intent(this, PostDetailActivity.class);
        intent.putExtra("postId", post.getPostId());
        startActivity(intent);
    }
}
