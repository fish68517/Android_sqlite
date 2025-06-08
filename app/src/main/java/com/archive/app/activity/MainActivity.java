package com.archive.app.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.archive.app.fragment.CategoryFragment;
import com.archive.app.fragment.NoteFragment;
import com.archive.app.fragment.ProfileFragment;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity  {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 默认加载首页Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoteFragment()).commit();
        }

        setupBottomNavigation();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new NoteFragment();
            } else if (itemId == R.id.nav_category) {
                selectedFragment = new CategoryFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}