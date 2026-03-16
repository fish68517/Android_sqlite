package com.hakimi.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.model.ApiResponse;
import com.hakimi.model.Comment;
import com.hakimi.model.PageResponse;
import com.hakimi.model.Post;
import com.hakimi.network.ApiService;
import com.hakimi.network.RetrofitClient;
import com.hakimi.ui.adapter.PostCommentAdapter;
import com.hakimi.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private PostCommentAdapter commentAdapter;

    private long postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        apiService = RetrofitClient.getInstance().getApiService();
        sharedPrefManager = SharedPrefManager.getInstance();

        postId = getIntent().getLongExtra(EXTRA_POST_ID, -1L);
        if (postId <= 0) {
            Toast.makeText(this, "\u5e16\u5b50\u53c2\u6570\u9519\u8bef", Toast.LENGTH_SHORT).show();
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

        tvPostAuthor.setText("\u7528\u6237#" + (userId > 0 ? userId : "-"));
        tvPostTime.setText(TextUtils.isEmpty(createdAt) ? "\u521a\u521a" : createdAt.replace("T", " "));
        tvPostContent.setText(TextUtils.isEmpty(content) ? "\u672a\u586b\u5199\u5185\u5bb9" : content);
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
        apiService.getPosts(1, 100).enqueue(new Callback<ApiResponse<PageResponse<Post>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PageResponse<Post>>> call,
                    @NonNull Response<ApiResponse<PageResponse<Post>>> response) {
                if (!response.isSuccessful() || response.body() == null
                        || !response.body().isSuccess() || response.body().getData() == null) {
                    Toast.makeText(CommentActivity.this, "\u52a0\u8f7d\u8bc4\u8bba\u5931\u8d25", Toast.LENGTH_SHORT).show();
                    showComments(new ArrayList<>());
                    return;
                }

                List<Post> posts = response.body().getData().getRecords();
                if (posts == null) {
                    showComments(new ArrayList<>());
                    return;
                }

                for (Post post : posts) {
                    if (post != null && post.getId() != null && post.getId().equals(postId)) {
                        List<Comment> comments = post.getComments() == null
                                ? new ArrayList<>()
                                : post.getComments();
                        showComments(comments);
                        return;
                    }
                }
                showComments(new ArrayList<>());
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PageResponse<Post>>> call, @NonNull Throwable t) {
                Toast.makeText(CommentActivity.this,
                        "\u52a0\u8f7d\u8bc4\u8bba\u5931\u8d25: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                showComments(new ArrayList<>());
            }
        });
    }

    private void showComments(List<Comment> comments) {
        commentAdapter.setComments(comments);
        tvEmptyComments.setVisibility(comments == null || comments.isEmpty() ? TextView.VISIBLE : TextView.GONE);
    }

    private void submitComment() {
        String content = etCommentContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "\u8bf7\u8f93\u5165\u8bc4\u8bba\u5185\u5bb9", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(this, "\u672a\u83b7\u53d6\u5230\u5f53\u524d\u7528\u6237", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);

        apiService.createComment(comment).enqueue(new Callback<ApiResponse<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Comment>> call,
                    @NonNull Response<ApiResponse<Comment>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    etCommentContent.setText("");
                    Toast.makeText(CommentActivity.this, "\u8bc4\u8bba\u6210\u529f", Toast.LENGTH_SHORT).show();
                    loadComments();
                } else {
                    Toast.makeText(CommentActivity.this, "\u8bc4\u8bba\u5931\u8d25", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Comment>> call, @NonNull Throwable t) {
                Toast.makeText(CommentActivity.this,
                        "\u8bc4\u8bba\u5931\u8d25: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HakimiApplication.curUser != null && HakimiApplication.curUser.getId() != null) {
            return HakimiApplication.curUser.getId();
        }
        return null;
    }
}
