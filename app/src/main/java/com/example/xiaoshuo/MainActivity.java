package com.example.xiaoshuo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.xiaoshuo.fragments.BookshelfFragment;
import com.example.xiaoshuo.fragments.CommunityFragment;
import com.example.xiaoshuo.fragments.DiscoverFragment;
import com.example.xiaoshuo.fragments.MineFragment;
import com.example.xiaoshuo.models.User;
import com.example.xiaoshuo.utils.AppUsageTracker;
import com.example.xiaoshuo.utils.ReadHistoryManager;
import com.example.xiaoshuo.utils.UserManager;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private TextView tvUsageTime;
    private AppUsageTracker usageTracker;
    private String currentFragmentTag = "";
    private UserManager userManager;
    private ReadHistoryManager readHistoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化管理器
        userManager = UserManager.getInstance(this);
        readHistoryManager = ReadHistoryManager.getInstance(this);
        
        // 初始化使用时长跟踪器
        usageTracker = AppUsageTracker.getInstance(this);
        
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        
        // 寻找使用时长文本框
        tvUsageTime = findViewById(R.id.tv_usage_time);
        
        // 让阅读历史管理器知道用户已更改
        readHistoryManager.onUserChanged();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        usageTracker.onAppStart();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateUsageTimeDisplay();
        
        // 检查用户是否登录，如果未登录则跳转到登录页面
        if (!userManager.isLoggedIn()) {
            Intent intent = new Intent(this, com.example.xiaoshuo.activities.LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        // 如果还未加载Fragment，或第一次加载应用，根据用户性别选择初始Fragment
        if (currentFragmentTag == null || currentFragmentTag.isEmpty()) {
            // 获取当前用户
            User user = userManager.getCurrentUser();
            if (user != null) {
                // 默认显示发现页面
                loadFragment(new DiscoverFragment());
                bottomNavigationView.setSelectedItemId(R.id.nav_discover);
            }
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (currentFragmentTag != null && !currentFragmentTag.isEmpty()) {
            usageTracker.trackPageEnd(currentFragmentTag);
        }
        usageTracker.onAppPause();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    /**
     * 更新使用时长显示
     */
    private void updateUsageTimeDisplay() {
        if (tvUsageTime != null) {
            // 显示今日使用时长
            String todayUsage = usageTracker.getTodayUsageTime();
            tvUsageTime.setText("今日使用: " + todayUsage);
        }
    }
    
    /**
     * 显示当前页面使用时长
     */
    public void showCurrentPageUsage() {
        if (currentFragmentTag != null && !currentFragmentTag.isEmpty()) {
            String pageUsage = usageTracker.getPageDuration(currentFragmentTag);
            Toast.makeText(this, currentFragmentTag + " 使用时长: " + pageUsage, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFragment(Fragment fragment) {
        // 结束上一个页面的时长统计
        if (currentFragmentTag != null && !currentFragmentTag.isEmpty()) {
            usageTracker.trackPageEnd(currentFragmentTag);
        }
        
        // 更新当前页面标签
        currentFragmentTag = fragment.getClass().getSimpleName();
        
        // 开始新页面的时长统计
        usageTracker.trackPageStart(currentFragmentTag);
        
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
        
        // 更新使用时长显示
        updateUsageTimeDisplay();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_shelf) {
            fragment = new BookshelfFragment();
        } else if (itemId == R.id.nav_discover) {
            fragment = new DiscoverFragment();
        } else if (itemId == R.id.nav_community) {
            fragment = new CommunityFragment();
        } else if (itemId == R.id.nav_mine) {
            fragment = new MineFragment();
        }

        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }
        return false;
    }
}