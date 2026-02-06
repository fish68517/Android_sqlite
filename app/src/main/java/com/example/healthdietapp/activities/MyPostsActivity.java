package com.example.healthdietapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.adapters.MyPostsAdapter;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.models.Post;
import com.example.healthdietapp.utils.SessionManager;

import java.util.List;

/**
 * MyPostsActivity - Displays user's published posts with delete functionality
 */
public class MyPostsActivity extends AppCompatActivity {

    private RecyclerView postsRecyclerView;
    private MyPostsAdapter adapter;
    
    private DatabaseHelper dbHelper;
    private PostDAO postDAO;
    private SessionManager sessionManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        initializeViews();
        initializeDatabase();
        setupRecyclerView();
        setupBackButton();
        loadUserPosts();
    }

    private void initializeViews() {
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        postDAO = new PostDAO(dbHelper);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void setupRecyclerView() {
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyPostsAdapter(this, post -> onPostClick(post), post -> onPostLongClick(post));
        postsRecyclerView.setAdapter(adapter);
    }

    private void setupBackButton() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void loadUserPosts() {
        new Thread(() -> {
            try {
                List<Post> posts = postDAO.getPostsByUser(userId);
                runOnUiThread(() -> {
                    if (posts != null && !posts.isEmpty()) {
                        adapter.updatePosts(posts);
                    } else {
                        Toast.makeText(MyPostsActivity.this, "No posts yet", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MyPostsActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void onPostClick(Post post) {
        // Navigate to post detail
        android.content.Intent intent = new android.content.Intent(this, PostDetailActivity.class);
        intent.putExtra("postId", post.getPostId());
        startActivity(intent);
    }

    private void onPostLongClick(Post post) {
        // Show delete confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> deletePost(post))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePost(Post post) {
        new Thread(() -> {
            try {
                boolean success = postDAO.deletePost(post.getPostId());
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(MyPostsActivity.this, "Post deleted", Toast.LENGTH_SHORT).show();
                        loadUserPosts();
                    } else {
                        Toast.makeText(MyPostsActivity.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MyPostsActivity.this, "Error deleting post", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
