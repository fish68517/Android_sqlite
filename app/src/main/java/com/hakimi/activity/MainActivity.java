package com.hakimi.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hakimi.R;

import com.hakimi.fragment.CommunityFragment;
import com.hakimi.fragment.DiaryFragment;
import com.hakimi.fragment.HomeFragment;
import com.hakimi.fragment.ProfileFragment;

/**
 * 主Activity
 * 
 * @author hakimi
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;

    private HomeFragment homeFragment;
    private CommunityFragment communityFragment;
    private DiaryFragment diaryFragment;

    private ProfileFragment mineFragment;

    // 定义一个请求码
    private static final int REQUEST_CODE_PERMISSION = 101;


    private void checkPermissionAndLoadImage() {

        // 1. 判断当前 Android 版本，确定需要申请哪个权限
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) +
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else { // Android 12 及以下
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        // 2. 检查是否已经授权
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            // 已经有权限，直接加载
            // loadImageWithGlide(url, imageView);
        } else {
            // 没有权限，去申请
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE_PERMISSION);
        }
    }


    // 4. 处理用户点击“允许/拒绝”的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户刚才点了“允许”，重新尝试加载
                // 注意：这里你需要知道刚才想加载哪个 url，通常建议把 url 存在一个全局变量或者重新触发加载逻辑
                Toast.makeText(this, "权限已获取，请重新操作", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要相册权限才能展示图片", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initFragments();
        checkPermissionAndLoadImage();
        Log.d("MainActivity", "HakimiApplication.getInstance().getUserId():000 " );

        String targetFragment = getIntent().getStringExtra("target_fragment");
        Log.d("MainActivity", "targetFragment: " + targetFragment);
        if ("MineFragment".equals(targetFragment)) {
            Log.d("MainActivity", "MineFragment");
            bottomNavigationView.setSelectedItemId(R.id.nav_mine);
            switchFragment(mineFragment);
        } else {
            Log.d("MainActivity", "HomeFragment2222222");
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            switchFragment(homeFragment);
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_community) {
                switchFragment(communityFragment);
                return true;
            } else if (itemId == R.id.nav_shopping) {
                switchFragment(diaryFragment);
                return true;
            } else if (itemId == R.id.nav_mine) {
                switchFragment(mineFragment);
                return true;
            }
            return false;
        });

        // 默认显示首页
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        communityFragment = new CommunityFragment();
        diaryFragment = new DiaryFragment();
        mineFragment = new ProfileFragment();

        // 默认显示首页
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
        currentFragment = homeFragment;
    }

    private void switchFragment(Fragment fragment) {
        if (fragment == currentFragment) {
            return;
        }

        if (fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment)
                    .show(fragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment)
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        currentFragment = fragment;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String targetFragment = intent.getStringExtra("target_fragment");
        Log.d("MainActivity", "targetFragment: " + targetFragment);
        if ("MineFragment".equals(targetFragment)) {
            Log.d("MainActivity", "MineFragment");
            bottomNavigationView.setSelectedItemId(R.id.nav_mine);
            switchFragment(mineFragment);
        } else {
            Log.d("MainActivity", "HomeFragment2222222");
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            switchFragment(homeFragment);
        }
    }
}

