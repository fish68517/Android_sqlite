package com.example.xiaoshuo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.activities.SearchActivity;
import com.example.xiaoshuo.adapters.CategoryPagerAdapter;
import com.example.xiaoshuo.adapters.RankingAdapter;
import com.example.xiaoshuo.models.Ranking;
import com.example.xiaoshuo.models.User;
import com.example.xiaoshuo.utils.BookDataManager;
import com.example.xiaoshuo.utils.UserManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class DiscoverFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private CardView searchBar;
    private RecyclerView rvRankings;
    private UserManager userManager;
    private CategoryPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        
        userManager = UserManager.getInstance(requireContext());
        
        initViews(view);
        setupViewPager();
        setupRankings();
        
        return view;
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        searchBar = view.findViewById(R.id.search_bar);
        rvRankings = view.findViewById(R.id.rv_rankings);
        
        searchBar.setOnClickListener(v -> {
            // 跳转到搜索页面
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
    }

    private void setupViewPager() {
        pagerAdapter = new CategoryPagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);
        
        // 将TabLayout和ViewPager2关联起来
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.category_male);
                    break;
                case 1:
                    tab.setText(R.string.category_female);
                    break;
                case 2:
                    tab.setText(R.string.category_listen);
                    break;
            }
        }).attach();
        
        // 根据用户性别自动选择对应的频道页面
        int initialPosition = 0; // 默认男生频道
        if (userManager.isLoggedIn()) {
            User currentUser = userManager.getCurrentUser();
            if (currentUser != null && "female".equals(currentUser.getGender())) {
                initialPosition = 1; // 女生频道
            }
        }
        
        // 设置初始页面
        viewPager.setCurrentItem(initialPosition, false);
    }
    
    private void setupRankings() {
        // 获取排行榜数据
        List<Ranking> rankings = BookDataManager.getRankings();
        
        // 设置布局管理器
        rvRankings.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 设置适配器
        RankingAdapter adapter = new RankingAdapter(getContext(), rankings);
        rvRankings.setAdapter(adapter);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 当页面恢复时，检查是否需要根据用户性别切换频道
        if (userManager.isLoggedIn() && viewPager != null) {
            User currentUser = userManager.getCurrentUser();
            if (currentUser != null) {
                int targetPosition = "female".equals(currentUser.getGender()) ? 1 : 0;
                if (viewPager.getCurrentItem() != targetPosition) {
                    viewPager.setCurrentItem(targetPosition, true);
                }
            }
        }
    }
} 