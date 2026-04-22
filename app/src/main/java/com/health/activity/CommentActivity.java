package com.Health.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Health.HealthApplication;
import com.Health.R;
import com.Health.adapter.PostCommentAdapter;
import com.Health.local.LocalHealthRepository;
import com.Health.local.LocalResult;
import com.Health.model.Comment;
import com.Health.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "extra_post_id";
    public static final String EXTRA_POST_CONTENT = "extra_post_content";
    public static final String EXTRA_POST_USER_ID = "extra_post_user_id";
    public static final String EXTRA_POST_CREATED_AT = "extra_post_created_at";

    private TextView tvPostAuthor;
    private TextView tvPostTime;
    private TextView tvPostContent;
    private RecyclerView rvComments;
    private TextView tvEmptyComments;
    private EditText etCommentContent;
    private Button btnSendComment;

    private LocalHealthRepository repository;
    private SharedPrefManager sharedPrefManager;
    private PostCommentAdapter commentAdapter;

    private long postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        repository = LocalHealthRepository.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance();

        postId = getIntent().getLongExtra(EXTRA_POST_ID, -1L);
        if (postId <= 0) {
            Toast.makeText(this, "帖子参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initPostHeader();
        initCommentsList();
        setupListeners();
        loadComments();
    }

    private void initViews() {
        tvPostAuthor = findViewById(R.id.tv_post_author);
        tvPostTime = findViewById(R.id.tv_post_time);
        tvPostContent = findViewById(R.id.tv_post_content);
        rvComments = findViewById(R.id.rv_comments);
        tvEmptyComments = findViewById(R.id.tv_empty_comments);
        etCommentContent = findViewById(R.id.et_comment_content);
        btnSendComment = findViewById(R.id.btn_send_comment);
    }

    private void initPostHeader() {
        long userId = getIntent().getLongExtra(EXTRA_POST_USER_ID, -1L);
        String createdAt = getIntent().getStringExtra(EXTRA_POST_CREATED_AT);
        String content = getIntent().getStringExtra(EXTRA_POST_CONTENT);

        tvPostAuthor.setText("用户#" + (userId > 0 ? userId : "-"));
        tvPostTime.setText(TextUtils.isEmpty(createdAt) ? "刚刚" : createdAt.replace("T", " "));
        tvPostContent.setText(TextUtils.isEmpty(content) ? "未填写内容" : content);
    }

    private void initCommentsList() {
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new PostCommentAdapter();
        rvComments.setAdapter(commentAdapter);
    }

    private void setupListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        btnSendComment.setOnClickListener(v -> submitComment());
    }

    private void loadComments() {
        List<Comment> comments = repository.getPostComments(postId);
        if (comments == null) {
            comments = new ArrayList<>();
        }
        showComments(comments);
    }

    private void showComments(List<Comment> comments) {
        commentAdapter.setComments(comments);
        tvEmptyComments.setVisibility(comments == null || comments.isEmpty() ? TextView.VISIBLE : TextView.GONE);
    }

    private void submitComment() {
        String content = etCommentContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(this, "未获取到当前用户", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalResult<Comment> result = repository.createComment(postId, userId, content);
        if (result.isSuccess()) {
            etCommentContent.setText("");
            Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
            loadComments();
        } else {
            Toast.makeText(this,
                    TextUtils.isEmpty(result.getMessage()) ? "评论失败" : result.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HealthApplication.curUser != null && HealthApplication.curUser.getId() != null) {
            return HealthApplication.curUser.getId();
        }
        return null;
    }
}
