package com.example.healthdietapp.activities;

import android.os.Bundle;
import android.util.Log;
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
import com.example.healthdietapp.utils.ImageUtils;
import com.example.healthdietapp.utils.SessionManager;

/**
 * PostDetailActivity - Displays detailed view of a post with interaction options
 */
public class PostDetailActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailActivity";

    // 顶部公共 Toolbar 控件
    private Button backButton;
    private TextView toolbarTitle;

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
        setupListeners();
    }

    private void initializeViews() {
        // 绑定 Toolbar 控件
        backButton = findViewById(R.id.backButton);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        // 设置自定义标题并绑定返回事件
        if (toolbarTitle != null) {
            toolbarTitle.setText("帖子详情");
        }
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                Log.d(TAG, "点击了返回键，关闭当前页面");
                finish();
            });
        }

        // 绑定其他帖子控件
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
        postId = getIntent().getStringExtra("postId");
        Log.d(TAG, "初始化完成, 当前 userId: " + userId + ", postId: " + postId);
    }

    private void loadPostData() {
        if (postId == null) {
            Toast.makeText(this, "帖子ID为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            post = postDAO.getPostById(postId);
            if (post != null) {
                Log.d(TAG, "成功加载帖子: " + post.getTitle());
                displayPost();
                updateInteractionButtonStates();
            } else {
                Log.w(TAG, "找不到对应ID的帖子: " + postId);
                Toast.makeText(this, "帖子加载失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "加载帖子数据时发生异常", e);
        }
    }

    private void displayPost() {
        postTitle.setText(post.getTitle());
        postContent.setText(post.getContent());
        postAuthor.setText("作者 ID: " + post.getUserId());
        postLikes.setText(post.getLikes() + " 点赞");

        // 这里同样可以利用和之前 Adapter 相似的逻辑去加载首张图片
        // 为了演示，这里暂时使用兜底灰色（如果你想显示相册图，可以参照 Adapter 的代码提取 img_post1.jpg 进行展示）

        ImageUtils.loadFirstImage(postImage, post.getImages());
    }

    private void setupListeners() {
        likeButton.setOnClickListener(v -> {
            AnimationUtils.applyButtonPressAnimation(likeButton, this::toggleLike);
        });

        collectButton.setOnClickListener(v -> {
            AnimationUtils.applyButtonPressAnimation(collectButton, this::toggleCollection);
        });

        followButton.setOnClickListener(v -> {
            AnimationUtils.applyButtonPressAnimation(followButton, this::toggleFollow);
        });
    }

    private void toggleLike() {
        try {
            boolean hasLiked = postDAO.hasUserLikedPost(userId, postId);
            boolean success;

            if (hasLiked) {
                success = postDAO.unlikePost(userId, postId);
            } else {
                success = postDAO.likePost(userId, postId);
            }

            if (success) {
                runOnUiThread(() -> {
                    updateInteractionButtonStates();
                    String message = hasLiked ? "已取消点赞" : "点赞成功";
                    Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                    // 重新加载并更新点赞数展示
                    loadPostData();
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "点赞操作异常", e);
        }
    }

    private void toggleCollection() {
        try {
            boolean hasCollected = postDAO.hasUserCollectedPost(userId, postId);
            boolean success;

            if (hasCollected) {
                success = postDAO.removeCollection(userId, postId);
            } else {
                success = postDAO.collectPost(userId, postId);
            }

            if (success) {
                updateInteractionButtonStates();
                String message = hasCollected ? "已取消收藏" : "收藏成功";
                Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "收藏操作异常", e);
        }
    }

    private void toggleFollow() {
        try {
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
                    String message = isFollowing ? "已取消关注" : "关注成功";
                    Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "关注操作异常", e);
        }
    }

    private void updateInteractionButtonStates() {
        try {
            boolean hasLiked = postDAO.hasUserLikedPost(userId, postId);
            boolean hasCollected = postDAO.hasUserCollectedPost(userId, postId);
            boolean isFollowing = postDAO.isUserFollowing(userId, post.getUserId());

            runOnUiThread(() -> {
                likeButton.setText(hasLiked ? "取消点赞" : "点赞");
                collectButton.setText(hasCollected ? "取消收藏" : "收藏");
                followButton.setText(isFollowing ? "取消关注" : "关注");
            });
        } catch (Exception e) {
            Log.e(TAG, "更新按钮状态异常", e);
        }
    }
}