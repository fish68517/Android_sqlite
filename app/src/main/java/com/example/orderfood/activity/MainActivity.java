package com.example.orderfood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.example.orderfood.R;
import com.example.orderfood.fragment.CommunityFragment;
import com.example.orderfood.fragment.OrderFragment;
import com.example.orderfood.fragment.ChatFragment;
import com.example.orderfood.fragment.HomeUserFragment;
import com.example.orderfood.fragment.ProflieFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            loadFragment(new HomeUserFragment());
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.setVisibility(View.GONE);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    searchEditText.setVisibility(View.GONE);
                    selectedFragment = new HomeUserFragment();
                    System.out.println("aaaaa 主页");
                } else if (id == R.id.nav_cart) {
                    System.out.println("aaaaa 种类");
                    searchEditText.setVisibility(View.GONE);
                    selectedFragment = new OrderFragment();
                } else if (id == R.id.nav_stufy) {
                    System.out.println("aaaaa 交流");
                    searchEditText.setVisibility(View.GONE);
                    // 交流
                    selectedFragment = new CommunityFragment();

                }
                else if (id == R.id.nav_mine) {
                    System.out.println("aaaaa 交流");
                    searchEditText.setVisibility(View.GONE);
                    selectedFragment = new ProflieFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });

        // 默认显示购物车Fragment
        bottomNavigationView.setSelectedItemId(R.id.nav_home);


        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String keyword = searchEditText.getText().toString();
                if (!keyword.isEmpty()) {
                    // 启动 RecommendationFragment

                }
                return true;
            }
            return false;
        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
