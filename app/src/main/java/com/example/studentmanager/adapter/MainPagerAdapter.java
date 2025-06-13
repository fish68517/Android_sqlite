package com.example.studentmanager.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.studentmanager.ui.StudentListFragment;
import com.example.studentmanager.ui.StatisticsFragment;
import com.example.studentmanager.ui.ClassListFragment;

/**
 * 主界面ViewPager适配器
 */
public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StudentListFragment();
            case 1:
                return new StatisticsFragment();
            case 2:
                return new ClassListFragment();
            default:
                throw new IllegalStateException("Unexpected value: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
} 