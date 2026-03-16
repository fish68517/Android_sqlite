package com.hakimi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.R;
import com.hakimi.utils.SharedPrefManager;

/**
 * 启动页Activity
 * 
 * @author hakimi
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2秒延迟

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // 检查是否已登录
            if (SharedPrefManager.getInstance().isLoggedIn()) {
                // 已登录，跳转到主界面
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            } else {
                // 未登录，跳转到登录页
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}

