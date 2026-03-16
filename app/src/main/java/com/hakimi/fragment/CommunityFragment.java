package com.hakimi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.model.ApiResponse;
import com.hakimi.model.Comment;
import com.hakimi.model.ExerciseData;
import com.hakimi.model.PageResponse;
import com.hakimi.model.Post;
import com.hakimi.network.ApiService;
import com.hakimi.network.RetrofitClient;
import com.hakimi.ui.activity.CommentActivity;
import com.hakimi.ui.activity.CreatePostActivity;
import com.hakimi.ui.adapter.PostAdapter;
import com.hakimi.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityFragment extends Fragment {

    private RecyclerView rvPosts;
    private FloatingActionButton fabAddPost;
    private TextView btnDailyCheckin;
    private PostAdapter postAdapter;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getInstance().getApiService();
        sharedPrefManager = SharedPrefManager.getInstance();

        rvPosts = view.findViewById(R.id.rv_community_posts);
        fabAddPost = view.findViewById(R.id.fab_add_post);
        btnDailyCheckin = view.findViewById(R.id.btn_daily_checkin);

        setupRecyclerView();
        setupListeners();
        loadPosts();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    private void setupRecyclerView() {
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(requireContext(), new ArrayList<>());
        postAdapter.setOnPostInteractionListener(new PostAdapter.OnPostInteractionListener() {
            @Override
            public void onLikeClick(Post post, int position) {
                likePost(post, position);
            }

            @Override
            public void onCommentClick(Post post, int position) {
                openCommentPage(post);
            }

            @Override
            public void onPostClick(Post post) {
                showPostDetail(post);
            }
        });
        rvPosts.setAdapter(postAdapter);
    }

    private void setupListeners() {
        fabAddPost.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreatePostActivity.class)));
        btnDailyCheckin.setOnClickListener(v -> dailyCheckIn());
    }

    private void loadPosts() {
        apiService.getPosts(1, 20).enqueue(new Callback<ApiResponse<PageResponse<Post>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Post>>> call,
                    Response<ApiResponse<PageResponse<Post>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()
                        && response.body().getData() != null) {
                    List<Post> posts = response.body().getData().getRecords();
                    postAdapter.setPosts(posts == null ? new ArrayList<>() : posts);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Post>>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "\u52a0\u8f7d\u5e16\u5b50\u5931\u8d25: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void likePost(Post post, int position) {
        if (post.getId() == null) {
            return;
        }
        apiService.likePost(post.getId()).enqueue(new Callback<ApiResponse<Post>>() {
            @Override
            public void onResponse(Call<ApiResponse<Post>> call, Response<ApiResponse<Post>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()
                        && response.body().getData() != null) {
                    postAdapter.updatePost(position, response.body().getData());
                } else {
                    Toast.makeText(getContext(), "\u70b9\u8d5e\u5931\u8d25", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Post>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "\u70b9\u8d5e\u5931\u8d25: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCommentPage(Post post) {
        if (post == null || post.getId() == null || getContext() == null) {
            return;
        }
        Intent intent = new Intent(getContext(), CommentActivity.class);
        intent.putExtra(CommentActivity.EXTRA_POST_ID, post.getId());
        intent.putExtra(CommentActivity.EXTRA_POST_CONTENT, post.getContent());
        intent.putExtra(CommentActivity.EXTRA_POST_USER_ID, post.getUserId() == null ? -1L : post.getUserId());
        intent.putExtra(CommentActivity.EXTRA_POST_CREATED_AT, post.getCreatedAt());
        startActivity(intent);
    }

    private void showPostDetail(Post post) {
        StringBuilder builder = new StringBuilder();
        if (post.getComments() == null || post.getComments().isEmpty()) {
            builder.append("\u6682\u65e0\u8bc4\u8bba");
        } else {
            for (Comment comment : post.getComments()) {
                builder.append("\u7528\u6237#")
                        .append(comment.getUserId())
                        .append(": ")
                        .append(comment.getContent())
                        .append("\n\n");
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("\u5e16\u5b50\u8bc4\u8bba")
                .setMessage(builder.toString().trim())
                .setPositiveButton("\u5173\u95ed", null)
                .show();
    }

    private void dailyCheckIn() {
        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(getContext(),
                    "\u672a\u83b7\u53d6\u5230\u5f53\u524d\u767b\u5f55\u7528\u6237",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ExerciseData exerciseData = new ExerciseData();
        exerciseData.setUserId(userId);
        exerciseData.setExerciseType("\u8dd1\u6b65\u6253\u5361");
        exerciseData.setLocation("\u6821\u56ed\u8fd0\u52a8\u573a");
        exerciseData.setDuration(30);

        apiService.dailyCheckIn(exerciseData).enqueue(new Callback<ApiResponse<ExerciseData>>() {
            @Override
            public void onResponse(Call<ApiResponse<ExerciseData>> call,
                    Response<ApiResponse<ExerciseData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(),
                            "\u4eca\u65e5\u8fd0\u52a8\u6253\u5361\u6210\u529f",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "\u6253\u5361\u5931\u8d25", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ExerciseData>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "\u6253\u5361\u5931\u8d25: " + t.getMessage(),
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
