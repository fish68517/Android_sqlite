package com.example.xiaoshuo.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.xiaoshuo.fragments.PostListFragment;

/**
 * 社区页面的ViewPager适配器
 */
public class CommunityPagerAdapter extends FragmentStateAdapter {

    public CommunityPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 根据位置创建不同类型的帖子列表Fragment
        switch (position) {
            case 0:
                return PostListFragment.newInstance(PostListFragment.TYPE_HOT);
            case 1:
                return PostListFragment.newInstance(PostListFragment.TYPE_LATEST);
            case 2:
                return PostListFragment.newInstance(PostListFragment.TYPE_DISCUSS);
            case 3:
                return PostListFragment.newInstance(PostListFragment.TYPE_REVIEW);
            case 4:
                return PostListFragment.newInstance(PostListFragment.TYPE_SEEK);
            default:
                return PostListFragment.newInstance(PostListFragment.TYPE_LATEST);
        }
    }

    @Override
    public int getItemCount() {
        // 返回页面数量
        return 5; // 热门、最新、讨论、书评、求书
    }
} 