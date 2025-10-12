package com.example.application;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.fragment.NavHostFragment;

import com.example.application.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Find the bottom navigation bar control
        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);

        // 2. Configure the top AppBar (if your app has one)
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_health, R.id.navigation_fitness, R.id.navigation_profile)
                .build();

        // 3. Find the navigation controller (NavController)
        // This is the recommended way to get the NavController in an Activity
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // 4. Connect the NavController and AppBarConfiguration (if you need to handle ActionBar title changes)
       // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // 5. Connect the BottomNavigationView and NavController
        NavigationUI.setupWithNavController(navView, navController);
    }
}