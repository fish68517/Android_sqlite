package com.example.xiaoshuo.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.xiaoshuo.fragments.ListenFragment;
import com.example.xiaoshuo.fragments.MaleFragment;
import com.example.xiaoshuo.fragments.FemaleFragment;
import com.example.xiaoshuo.models.User;
import com.example.xiaoshuo.utils.UserManager;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    private UserManager userManager;
    private String userGender;

    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        userManager = UserManager.getInstance(fragmentActivity);
        
        // 获取用户性别，默认为男生
        userGender = "male";
        if (userManager.isLoggedIn() && userManager.getCurrentUser() != null) {
            userGender = userManager.getCurrentUser().getGender();
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MaleFragment();
            case 1:
                return new FemaleFragment();
            case 2:
                return new ListenFragment();
            default:
                return new MaleFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 男生、女生、听书三个选项卡
    }
    
    /**
     * 根据用户性别获取初始页面位置
     * @return 初始页面位置
     */
    public int getInitialPosition() {
        if ("female".equals(userGender)) {
            return 1; // 女生页面
        } else {
            return 0; // 男生页面
        }
    }
} 