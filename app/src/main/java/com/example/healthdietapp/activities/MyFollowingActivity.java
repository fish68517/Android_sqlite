package com.example.healthdietapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.adapters.MyFollowingAdapter;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.database.UserDAO;
import com.example.healthdietapp.models.User;
import com.example.healthdietapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * MyFollowingActivity - Displays list of followed authors with unfollow functionality
 */
public class MyFollowingActivity extends AppCompatActivity {

    private RecyclerView followingRecyclerView;
    private MyFollowingAdapter adapter;
    
    private DatabaseHelper dbHelper;
    private PostDAO postDAO;
    private UserDAO userDAO;
    private SessionManager sessionManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_following);

        initializeViews();
        initializeDatabase();
        setupRecyclerView();
        setupBackButton();
        loadFollowingAuthors();
    }

    private void initializeViews() {
        followingRecyclerView = findViewById(R.id.followingRecyclerView);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        postDAO = new PostDAO(dbHelper);
        userDAO = new UserDAO(dbHelper);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void setupRecyclerView() {
        followingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyFollowingAdapter(new ArrayList<>(), 
                this::onAuthorClick, 
                this::onUnfollowClick);
        followingRecyclerView.setAdapter(adapter);
    }

    private void setupBackButton() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void loadFollowingAuthors() {
        new Thread(() -> {
            try {
                // Get list of user IDs that current user is following
                List<String> followingIds = postDAO.getUserFollowing(userId);
                
                // Get User objects for each following ID
                List<User> followingUsers = new ArrayList<>();
                for (String followeeId : followingIds) {
                    User user = userDAO.getUserById(followeeId);
                    if (user != null) {
                        followingUsers.add(user);
                    }
                }
                
                runOnUiThread(() -> {
                    if (followingUsers != null && !followingUsers.isEmpty()) {
                        adapter.updateFollowingUsers(followingUsers);
                    } else {
                        Toast.makeText(MyFollowingActivity.this, "No following yet", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MyFollowingActivity.this, "Failed to load following", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void onAuthorClick(User author) {
        // Navigate to author's profile page
        Intent intent = new Intent(this, AuthorProfileActivity.class);
        intent.putExtra("userId", author.getUserId());
        startActivity(intent);
    }

    private void onUnfollowClick(User author) {
        // Show unfollow confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Unfollow")
                .setMessage("Are you sure you want to unfollow " + author.getNickname() + "?")
                .setPositiveButton("Unfollow", (dialog, which) -> unfollowAuthor(author))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void unfollowAuthor(User author) {
        new Thread(() -> {
            try {
                boolean success = postDAO.unfollowUser(userId, author.getUserId());
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(MyFollowingActivity.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                        loadFollowingAuthors();
                    } else {
                        Toast.makeText(MyFollowingActivity.this, "Failed to unfollow", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MyFollowingActivity.this, "Error unfollowing", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
