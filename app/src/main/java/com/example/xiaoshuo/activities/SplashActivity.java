package com.example.xiaoshuo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xiaoshuo.MainActivity;
import com.example.xiaoshuo.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make the activity fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_splash);

        // Hide the action bar if it's present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Use Handler to delay moving to the main activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start main activity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            
            // Close splash activity
            finish();
        }, SPLASH_DURATION);
    }
} 