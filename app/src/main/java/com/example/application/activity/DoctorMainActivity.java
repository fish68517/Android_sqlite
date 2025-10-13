package com.example.application.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.application.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DoctorMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        BottomNavigationView navView = findViewById(R.id.bottom_nav_view_doctor);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_doctor);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(navView, navController);
    }
}