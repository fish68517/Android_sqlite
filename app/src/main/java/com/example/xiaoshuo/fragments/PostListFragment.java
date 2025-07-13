package com.example.xiaoshuo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.activities.PostDetailActivity;
import com.example.xiaoshuo.adapters.PostAdapter;
import com.example.xiaoshuo.models.Post;
import com.example.xiaoshuo.utils.CommunityDataManager;

import java.util.ArrayList;
import java.util.List;

public class PostListFragment extends Fragment implements PostAdapter.OnPostClickListener {

    public static final int TYPE_HOT = 0;
    public static final int TYPE_LATEST = 1;
    public static final int TYPE_DISCUSS = 2;
    public static final int TYPE_REVIEW = 3;
    public static final int TYPE_SEEK = 4;

    private int type;
    private RecyclerView rvPosts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter postAdapter;
    private CommunityDataManager dataManager;
    private List<Post> postList = new ArrayList<>();

    public static PostListFragment newInstance(int type) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type", TYPE_LATEST);
        }
        dataManager = CommunityDataManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        initViews(view);
        loadData();
        return view;
    }

    private void initViews(View view) {
        rvPosts = view.findViewById(R.id.rv_posts);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // 设置RecyclerView
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), postList);
        postAdapter.setOnPostClickListener(this);
        rvPosts.setAdapter(postAdapter);

        // 设置下拉刷新
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        // 根据类型加载不同的数据
        switch (type) {
            case TYPE_HOT:
                postList = dataManager.getHotPosts();
                break;
            case TYPE_LATEST:
                postList = dataManager.getLatestPosts();
                break;
            case TYPE_DISCUSS:
                postList = dataManager.getPostsByType("讨论");
                break;
            case TYPE_REVIEW:
                postList = dataManager.getPostsByType("书评");
                break;
            case TYPE_SEEK:
                postList = dataManager.getPostsByType("求书");
                break;
            default:
                postList = dataManager.getLatestPosts();
                break;
        }

        // 更新UI
        postAdapter.updateData(postList);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPostClick(Post post) {
        // 跳转到帖子详情页
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra("post_id", post.getId());
        startActivity(intent);
    }

    @Override
    public void onLikeClick(Post post, int position) {
        // 点赞/取消点赞
        dataManager.likePost(post.getId());
        postAdapter.notifyItemChanged(position);
    }

    @Override
    public void onCommentClick(Post post) {
        // 跳转到帖子详情页并定位到评论区
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra("post_id", post.getId());
        intent.putExtra("scroll_to_comment", true);
        startActivity(intent);
    }

    @Override
    public void onShareClick(Post post) {
        // 分享帖子
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, post.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, post.getTitle() + "\n\n" + post.getContent() + "\n\n—— 来自小说阅读APP");
        startActivity(Intent.createChooser(shareIntent, "分享帖子"));
    }
} 