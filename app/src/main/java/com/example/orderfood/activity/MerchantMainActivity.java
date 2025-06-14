package com.example.orderfood.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.orderfood.R;
import com.example.orderfood.fragment.MerchantDishesFragment;
import com.example.orderfood.fragment.MerchantStatsFragment;
import com.example.orderfood.fragment.OrderFragmentMerchant;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MerchantMainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_main);

        initViews();
        setupNavigation();
        
        // 默认显示菜品管理页面
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, new MerchantDishesFragment())
            .commit();
    }

    private void initViews() {
        bottomNav = findViewById(R.id.bottomNav);
    }

    private void setupNavigation() {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                
                if (item.getItemId() == R.id.nav_dishes) {
                    selectedFragment = new MerchantDishesFragment();
                } else if (item.getItemId() == R.id.nav_stats) {
                    selectedFragment = new MerchantStatsFragment();
                } else if (item.getItemId() == R.id.nav_order) {
                    selectedFragment = new OrderFragmentMerchant();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
                    return true;
                }
                return false;
            }
        });
    }
} 