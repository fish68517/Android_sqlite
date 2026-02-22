package com.example.healthdietapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.utils.SessionManager;

/**
 * Splash Activity - Initial screen shown when app starts
 * Checks login status and navigates to appropriate screen
 * If user is logged in, navigates to MainActivity
 * If user is not logged in, navigates to LoginActivity
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2 seconds
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Delay and then check login status
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkLoginStatus();
        }, SPLASH_DELAY);
    }

    private void checkLoginStatus() {
        // Check if user is logged in
        if (sessionManager.isLoggedIn()) {
            // User is logged in - navigate to MainActivity
            navigateToLogin();
        } else {
            // User is not logged in - navigate to LoginActivity
            navigateToLogin();
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
