package com.example.xiaoshuo.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.activities.PostDetailActivity;
import com.example.xiaoshuo.adapters.CommunityPagerAdapter;
import com.example.xiaoshuo.models.Post;
import com.example.xiaoshuo.utils.CommunityDataManager;
import com.example.xiaoshuo.utils.UserManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Date;

public class CommunityFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabNewPost;
    private CommunityPagerAdapter pagerAdapter;
    private CommunityDataManager dataManager;
    private UserManager userManager;

    private final String[] tabTitles = new String[]{"热门", "最新", "讨论", "书评", "求书"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        
        dataManager = CommunityDataManager.getInstance();
        userManager = UserManager.getInstance(requireContext());
        
        initViews(view);
        setupViewPager();
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        fabNewPost = view.findViewById(R.id.fab_new_post);
    }

    private void setupViewPager() {
        // 创建适配器
        pagerAdapter = new CommunityPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // 关联TabLayout和ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    private void setupListeners() {
        // 发布新帖子按钮点击事件
        fabNewPost.setOnClickListener(v -> {
            if (userManager.isLoggedIn()) {
                showNewPostDialog();
            } else {
                showLoginDialog();
            }
        });
    }

    /**
     * 显示发布新帖子对话框
     */
    private void showNewPostDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_post, null);
        EditText etTitle = dialogView.findViewById(R.id.et_post_title);
        EditText etContent = dialogView.findViewById(R.id.et_post_content);
        TabLayout tabPostType = dialogView.findViewById(R.id.tab_post_type);
        
        // 添加帖子类型选项卡
        tabPostType.addTab(tabPostType.newTab().setText("讨论"));
        tabPostType.addTab(tabPostType.newTab().setText("书评"));
        tabPostType.addTab(tabPostType.newTab().setText("求书"));
        tabPostType.addTab(tabPostType.newTab().setText("分享"));
        tabPostType.addTab(tabPostType.newTab().setText("活动"));
        
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("发布新帖")
                .setView(dialogView)
                .setPositiveButton("发布", (dialogInterface, i) -> {
                    // 获取输入内容
                    String title = etTitle.getText().toString().trim();
                    String content = etContent.getText().toString().trim();
                    String postType = tabPostType.getTabAt(tabPostType.getSelectedTabPosition()).getText().toString();
                    
                    // 验证输入
                    if (title.isEmpty()) {
                        Toast.makeText(getContext(), "标题不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (content.isEmpty()) {
                        Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // 创建新帖子
                    long postId = dataManager.getAllPosts().size() + 1;
                    Post newPost = new Post(
                            postId,
                            title,
                            content,
                            userManager.getCurrentUser().getUsername(),
                            userManager.getCurrentUser().getAvatarUrl()
                    );
                    newPost.setPostType(postType);
                    newPost.setPublishTime(new Date());
                    
                    // 添加帖子
                    dataManager.addPost(newPost);
                    
                    // 刷新页面
                    pagerAdapter.notifyDataSetChanged();
                    
                    // 跳转到帖子详情页
                    Intent intent = new Intent(getContext(), PostDetailActivity.class);
                    intent.putExtra("post_id", postId);
                    startActivity(intent);
                    
                    Toast.makeText(getContext(), "发布成功", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .create();
        
        dialog.show();
    }

    /**
     * 显示登录提示对话框
     */
    private void showLoginDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("发布帖子需要先登录，是否前往登录页面？")
                .setPositiveButton("去登录", (dialog, which) -> {
                    // 跳转到登录页面
                    Intent intent = new Intent(getActivity(), com.example.xiaoshuo.activities.LoginActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
    }
} 