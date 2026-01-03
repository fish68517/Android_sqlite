package com.myapplication.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.myapplication.app.fragment.CourseFragment;
import com.myapplication.app.fragment.ExamFragment;
import com.myapplication.app.fragment.HomeworkFragment;
import com.myapplication.app.fragment.MediaFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // 默认显示课程页面
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new CourseFragment()).commit();
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_course) {
                        selectedFragment = new CourseFragment();
                    } else if (itemId == R.id.nav_homework) {
                        selectedFragment = new HomeworkFragment();
                    } else if (itemId == R.id.nav_exam) {
                        selectedFragment = new ExamFragment();
                    } else {
                        selectedFragment = new MediaFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }
                    return true;
                }
            };
}