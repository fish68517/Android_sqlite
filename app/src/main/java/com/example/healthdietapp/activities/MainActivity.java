package com.example.healthdietapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthdietapp.R;
import com.example.healthdietapp.fragments.CategoryFragment;
import com.example.healthdietapp.fragments.DiscoverFragment;
import com.example.healthdietapp.fragments.HomeFragment;
import com.example.healthdietapp.fragments.MoreFragment;
import com.example.healthdietapp.fragments.ProfileFragment;
import com.example.healthdietapp.utils.AnimationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main Activity - Primary container for the application
 * Manages bottom navigation and fragment switching
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupBottomNavigation();
        
        // Load default fragment if not restoring from saved state
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void initializeViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_category) {
                selectedFragment = new CategoryFragment();
            } else if (itemId == R.id.nav_discover) {
                selectedFragment = new DiscoverFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_more) {
                selectedFragment = new MoreFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        // Set default selection
        bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    private void loadFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        
        currentFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // Add fade transition animation
        transaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
        );
        
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
