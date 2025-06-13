package com.example.studentmanager.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.studentmanagement.R;
import com.example.studentmanager.service.StudentStatusUpdateService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private StatusUpdateReceiver statusUpdateReceiver;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
        
        // 设置默认选中的Fragment
        if (savedInstanceState == null) {
            switchFragment(new StudentListFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_student_list);
        }
        
        // 初始化并注册广播接收器
        statusUpdateReceiver = new StatusUpdateReceiver();
        IntentFilter filter = new IntentFilter(StudentStatusUpdateService.ACTION_STATUS_UPDATED);
        registerReceiver(statusUpdateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收器
        if (statusUpdateReceiver != null) {
            unregisterReceiver(statusUpdateReceiver);
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupListeners() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_student_list) {
                selectedFragment = new StudentListFragment();
            } else if (itemId == R.id.nav_statistics) {
                selectedFragment = new StatisticsFragment();
            } else if (itemId == R.id.nav_class_list) {
                selectedFragment = new ClassListFragment();
            }
            
            if (selectedFragment != null) {
                switchFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }
    
    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            // 退出登录
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.action_update_status) {
            // 启动学籍状态更新服务
            Toast.makeText(this, "正在后台执行毕业检查...", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(this, StudentStatusUpdateService.class);
            startService(serviceIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // 内部类广播接收器
    private class StatusUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (StudentStatusUpdateService.ACTION_STATUS_UPDATED.equals(intent.getAction())) {
                int updatedCount = intent.getIntExtra(StudentStatusUpdateService.EXTRA_UPDATED_COUNT, 0);
                String message;
                if (updatedCount > 0) {
                    message = "检查完成！" + updatedCount + "名学生的学籍已更新为毕业。";
                } else {
                    message = "检查完成！没有学生的学籍状态需要更新。";
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                // 刷新当前页面的数据
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof StudentListFragment) {
                    ((StudentListFragment) currentFragment).onResume();
                } else if (currentFragment instanceof ClassListFragment) {
                    ((ClassListFragment) currentFragment).onResume();
                }
            }
        }
    }
} 