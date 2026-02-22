package com.example.healthdietapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthdietapp.R;
import com.example.healthdietapp.adapters.CollectionPagerAdapter;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.PostDAO;
import com.example.healthdietapp.database.RecipeDAO;
import com.example.healthdietapp.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * MyCollectionsActivity - Displays user's collected items (recipes, posts, questions)
 */
public class MyLikesActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    
    private DatabaseHelper dbHelper;
    private PostDAO postDAO;
    private RecipeDAO recipeDAO;
    private SessionManager sessionManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collections);

        initializeViews();
        initializeDatabase();
        setupBackButton();
        setupViewPager();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        postDAO = new PostDAO(dbHelper);
        recipeDAO = new RecipeDAO(dbHelper);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void setupBackButton() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void setupViewPager() {
        CollectionPagerAdapter adapter = new CollectionPagerAdapter(this, userId, postDAO, recipeDAO);
        viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("点赞帖子");
                    break;
                case 1:
                    tab.setText("点赞帖子");
                    break;
                case 2:
                    tab.setText("Questions");
                    break;
            }
        }).attach();
    }
}
