package com.example.orderfood.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.orderfood.R;
import com.example.orderfood.fragment.MerchantListFragment;
import com.example.orderfood.fragment.MerchantManagerListFragment;
import com.example.orderfood.fragment.MerchantRegisterFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMerchantActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_merchant);

        // 初始化视图
        initViews();
        
        // 默认显示商家列表页面
        loadFragment(new MerchantListFragment());
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.nav_merchant_list) {
                fragment = new MerchantManagerListFragment();
            } else if (item.getItemId() == R.id.nav_merchant_register) {
                fragment = new MerchantRegisterFragment();
            }
            
            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
} 