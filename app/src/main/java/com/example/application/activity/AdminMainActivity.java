package com.example.application.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.application.R;
import com.google.android.material.navigation.NavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        Toolbar toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_admin);
        NavigationView navigationView = findViewById(R.id.nav_view_admin);

        // 设置 NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_admin);
        navController = navHostFragment.getNavController();

        // 将 NavigationView 与 NavController 关联
        NavigationUI.setupWithNavController(navigationView, navController);

        // 设置 ActionBarDrawerToggle (汉堡图标)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 更新导航栏头部的用户名
        updateNavHeader();
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view_admin);
        View headerView = navigationView.getHeaderView(0);
        TextView tvUsername = headerView.findViewById(R.id.tv_admin_username_header);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String username = prefs.getString("LOGGED_IN_USERNAME", "管理员");
        tvUsername.setText(username);
    }

    // 处理向上导航
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp();
    }

    // 处理返回键
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}