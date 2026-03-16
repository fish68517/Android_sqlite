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
import com.hakimi.activity.CommentActivity;
import com.hakimi.activity.CreatePostActivity;
import com.hakimi.adapter.PostAdapter;
import com.hakimi.local.LocalHealthRepository;
import com.hakimi.local.LocalResult;
import com.hakimi.model.Comment;
import com.hakimi.model.Post;
import com.hakimi.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private RecyclerView rvPosts;
    private FloatingActionButton fabAddPost;
    private TextView btnDailyCheckin;
    private PostAdapter postAdapter;
    private LocalHealthRepository repository;
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

        repository = LocalHealthRepository.getInstance(requireContext());
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
        List<Post> posts = repository.getPosts(20);
        postAdapter.setPosts(posts == null ? new ArrayList<>() : posts);
    }

    private void likePost(Post post, int position) {
        if (post.getId() == null) {
            return;
        }
        LocalResult<Post> result = repository.likePost(post.getId());
        if (result.isSuccess() && result.getData() != null) {
            postAdapter.updatePost(position, result.getData());
        } else {
            Toast.makeText(getContext(), "点赞失败", Toast.LENGTH_SHORT).show();
        }
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
            builder.append("暂无评论");
        } else {
            for (Comment comment : post.getComments()) {
                builder.append("用户#")
                        .append(comment.getUserId())
                        .append(": ")
                        .append(comment.getContent())
                        .append("\n\n");
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("帖子评论")
                .setMessage(builder.toString().trim())
                .setPositiveButton("关闭", null)
                .show();
    }

    private void dailyCheckIn() {
        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(getContext(), "未获取到当前登录用户", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalResult<?> result = repository.addExerciseRecord(userId, "跑步打卡", "校园运动场", 30);
        if (result.isSuccess()) {
            Toast.makeText(getContext(), "今日运动打卡成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "打卡失败", Toast.LENGTH_SHORT).show();
        }
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
