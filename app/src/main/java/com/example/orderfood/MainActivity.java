package com.example.orderfood;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.application.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 找到底部导航栏控件
        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);

        // 2. 配置顶部的 AppBar（如果你的应用有的话），这里我们定义了顶级的几个目的地
        // 即使没有AppBar，这一步对于正确处理Fragment切换也很重要
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_health, R.id.navigation_fitness, R.id.navigation_profile)
                .build();

        // 3. 找到导航控制器 (NavController)
        // 它与我们在 XML 中定义的 NavHostFragment 关联
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // 4. 将 NavController 和 AppBarConfiguration 关联起来 (如果需要处理ActionBar的标题变化)
        // 如果你没有ActionBar，这行可以省略，但保留它是个好习惯
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // 5. 将底部导航栏 (BottomNavigationView) 和 NavController 关联起来
        // 这是最关键的一步，它使得点击导航项时能自动切换 Fragment
        NavigationUI.setupWithNavController(navView, navController);
    }
}