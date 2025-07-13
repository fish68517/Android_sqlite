package com.example.xiaoshuo.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xiaoshuo.R;
import com.example.xiaoshuo.adapters.CommentAdapter;
import com.example.xiaoshuo.models.Comment;
import com.example.xiaoshuo.models.Post;
import com.example.xiaoshuo.utils.CommunityDataManager;
import com.example.xiaoshuo.utils.UserManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity implements CommentAdapter.OnCommentClickListener {

    private Toolbar toolbar;
    private CircleImageView ivAuthorAvatar;
    private TextView tvAuthorName;
    private TextView tvPublishTime;
    private TextView tvPostType;
    private TextView tvPostTitle;
    private TextView tvPostContent;
    private ImageView ivLike;
    private TextView tvLikeCount;
    private TextView tvCommentCount;
    private TextView tvViewCount;
    private RecyclerView rvComments;
    private EditText etComment;
    private View btnSend;
    
    private CommentAdapter commentAdapter;
    private CommunityDataManager dataManager;
    private UserManager userManager;
    private Post currentPost;
    private Comment replyToComment; // 当前要回复的评论

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        
        dataManager = CommunityDataManager.getInstance();
        userManager = UserManager.getInstance(this);
        
        // 获取帖子ID
        long postId = getIntent().getLongExtra("post_id", -1);
        if (postId == -1) {
            Toast.makeText(this, "帖子不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // 获取帖子数据
        currentPost = dataManager.getPostById(postId);
        if (currentPost == null) {
            Toast.makeText(this, "帖子不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupToolbar();
        displayPostData();
        setupCommentList();
        setupCommentInput();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivAuthorAvatar = findViewById(R.id.iv_author_avatar);
        tvAuthorName = findViewById(R.id.tv_author_name);
        tvPublishTime = findViewById(R.id.tv_publish_time);
        tvPostType = findViewById(R.id.tv_post_type);
        tvPostTitle = findViewById(R.id.tv_post_title);
        tvPostContent = findViewById(R.id.tv_post_content);
        ivLike = findViewById(R.id.iv_like);
        tvLikeCount = findViewById(R.id.tv_like_count);
        tvCommentCount = findViewById(R.id.tv_comment_count);
        tvViewCount = findViewById(R.id.tv_view_count);
        rvComments = findViewById(R.id.rv_comments);
        etComment = findViewById(R.id.et_comment);
        btnSend = findViewById(R.id.btn_send);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("帖子详情");
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayPostData() {
        // 设置作者信息
        tvAuthorName.setText(currentPost.getAuthorName());
        if (currentPost.getAuthorAvatar() != null && !currentPost.getAuthorAvatar().isEmpty()) {
            Glide.with(this)
                .load(currentPost.getAuthorAvatar())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(ivAuthorAvatar);
        }
        
        // 设置帖子内容
        tvPostType.setText(currentPost.getPostType());
        tvPostTitle.setText(currentPost.getTitle());
        tvPostContent.setText(currentPost.getContent());
        
        // 设置发布时间
        tvPublishTime.setText(CommunityDataManager.formatTimeAgo(currentPost.getPublishTime()));
        
        // 设置互动数据
        tvLikeCount.setText(String.valueOf(currentPost.getLikeCount()));
        tvCommentCount.setText(String.valueOf(currentPost.getCommentCount()));
        tvViewCount.setText(String.valueOf(currentPost.getViewCount()) + " 浏览");
        
        // 设置点赞状态
        updateLikeStatus();
        
        // 设置点赞点击事件
        ivLike.setOnClickListener(v -> {
            if (userManager.isLoggedIn()) {
                dataManager.likePost(currentPost.getId());
                updateLikeStatus();
            } else {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 增加浏览量
        currentPost.setViewCount(currentPost.getViewCount() + 1);
    }

    private void updateLikeStatus() {
        if (currentPost.isLiked()) {
            ivLike.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            ivLike.setImageResource(android.R.drawable.btn_star_big_off);
        }
        tvLikeCount.setText(String.valueOf(currentPost.getLikeCount()));
    }

    private void setupCommentList() {
        // 设置评论列表
        commentAdapter = new CommentAdapter(this, currentPost.getComments());
        commentAdapter.setOnCommentClickListener(this);
        
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);
    }

    private void setupCommentInput() {
        // 设置评论输入
        btnSend.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!userManager.isLoggedIn()) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 创建新评论
            long commentId = currentPost.getComments().size() + 1;
            Comment newComment = new Comment(
                    commentId,
                    currentPost.getId(),
                    content,
                    userManager.getCurrentUser().getUsername(),
                    userManager.getCurrentUser().getAvatarUrl()
            );
            
            // 如果是回复评论
            if (replyToComment != null) {
                newComment.setReplyToCommentId(replyToComment.getId());
                newComment.setReplyToUserName(replyToComment.getAuthorName());
                replyToComment = null;
                etComment.setHint("发表评论");
            }
            
            // 添加评论
            dataManager.addComment(newComment);
            
            // 更新UI
            commentAdapter.updateData(currentPost.getComments());
            tvCommentCount.setText(String.valueOf(currentPost.getCommentCount()));
            
            // 清空输入框
            etComment.setText("");
            
            Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onCommentClick(Comment comment) {
        // 点击评论，暂不处理
    }

    @Override
    public void onLikeClick(Comment comment, int position) {
        if (userManager.isLoggedIn()) {
            // 点赞/取消点赞评论
            comment.setLiked(!comment.isLiked());
            if (comment.isLiked()) {
                comment.setLikeCount(comment.getLikeCount() + 1);
            } else {
                comment.setLikeCount(comment.getLikeCount() - 1);
            }
            commentAdapter.notifyItemChanged(position);
        } else {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReplyClick(Comment comment) {
        if (userManager.isLoggedIn()) {
            // 设置回复对象
            replyToComment = comment;
            etComment.setHint("回复 @" + comment.getAuthorName());
            etComment.requestFocus();
        } else {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
        }
    }
} 