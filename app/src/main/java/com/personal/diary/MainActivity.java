package com.personal.diary;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private SessionManager session;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        if (session.isAdmin()) {
            setupAdminMain();
            return;
        }
        setupUserMain();
    }

    private void setupUserMain() {
        setContentView(R.layout.activity_main);
        titleText = findViewById(R.id.titleText);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_diary) {
                switchFragment(new DiaryFragment(), "心情日记");
            } else if (id == R.id.nav_moment) {
                switchFragment(new MomentFragment(), "幸福瞬间");
            } else if (id == R.id.nav_tree_hole) {
                switchFragment(new TreeHoleFragment(), "树洞");
            } else if (id == R.id.nav_search) {
                switchFragment(new SearchFragment(), "搜索");
            } else if (id == R.id.nav_profile) {
                switchFragment(new ProfileFragment(), "我的");
            }
            return true;
        });
        bottomNavigation.setSelectedItemId(R.id.nav_diary);
    }

    private void setupAdminMain() {
        setContentView(R.layout.activity_admin_main);
        TextView adminTitleText = findViewById(R.id.adminTitleText);
        DrawerLayout drawerLayout = findViewById(R.id.adminDrawerLayout);
        NavigationView navigationView = findViewById(R.id.adminNavigationView);
        findViewById(R.id.adminMenuButton).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment;
            if (id == R.id.admin_nav_stats) {
                fragment = new AdminDashboardFragment();
            } else if (id == R.id.admin_nav_users) {
                fragment = AdminManageFragment.newInstance("users");
            } else if (id == R.id.admin_nav_diaries) {
                fragment = AdminManageFragment.newInstance("diaries");
            } else if (id == R.id.admin_nav_moments) {
                fragment = AdminManageFragment.newInstance("moments");
            } else if (id == R.id.admin_nav_tree_holes) {
                fragment = AdminManageFragment.newInstance("tree_holes");
            } else if (id == R.id.admin_nav_posts) {
                fragment = AdminManageFragment.newInstance("posts");
            } else if (id == R.id.admin_nav_feedback) {
                fragment = AdminManageFragment.newInstance("feedback");
            } else if (id == R.id.admin_nav_notices) {
                fragment = AdminManageFragment.newInstance("notices");
            } else if (id == R.id.admin_nav_logout) {
                logout();
                return true;
            } else {
                fragment = new AdminDashboardFragment();
            }
            adminTitleText.setText(item.getTitle());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, fragment)
                    .commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        navigationView.setCheckedItem(R.id.admin_nav_stats);
        adminTitleText.setText(navigationView.getMenu().findItem(R.id.admin_nav_stats).getTitle());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminFragmentContainer, new AdminDashboardFragment())
                .commit();
    }

    private void switchFragment(Fragment fragment, String title) {
        titleText.setText(title);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void logout() {
        session.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
