package com.example.campusguide;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.campusguide.fragment.FavoritesFragment;
import com.example.campusguide.fragment.HomeFragment;
import com.example.campusguide.fragment.PlacesFragment;
import com.example.campusguide.fragment.ProfileFragment;
import com.example.campusguide.fragment.RoutesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarTitle = findViewById(R.id.text_toolbar_title);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> selectPage(item.getItemId()));

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private boolean selectPage(int itemId) {
        Fragment fragment;
        String title;
        if (itemId == R.id.nav_places) {
            fragment = new PlacesFragment();
            title = "校园地点";
        } else if (itemId == R.id.nav_routes) {
            fragment = new RoutesFragment();
            title = "推荐路线";
        } else if (itemId == R.id.nav_favorites) {
            fragment = new FavoritesFragment();
            title = "我的收藏";
        } else if (itemId == R.id.nav_profile) {
            fragment = new ProfileFragment();
            title = "个人中心";
        } else {
            fragment = new HomeFragment();
            title = "校园导览";
        }

        toolbarTitle.setText(title);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return true;
    }

    public void openTab(int menuItemId) {
        bottomNavigation.setSelectedItemId(menuItemId);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
