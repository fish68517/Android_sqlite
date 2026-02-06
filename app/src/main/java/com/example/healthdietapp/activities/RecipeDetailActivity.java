package com.example.healthdietapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthdietapp.R;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.RecipeDAO;
import com.example.healthdietapp.models.Recipe;
import com.example.healthdietapp.utils.AnimationUtils;

/**
 * RecipeDetailActivity - Displays detailed information about a recipe
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private TextView recipeName;
    private TextView recipeDescription;
    private TextView recipeIngredients;
    private TextView recipeInstructions;
    private TextView recipeNutrition;
    private Button addRecipeButton;
    private Button backButton;
    private ScrollView recipeScrollView;

    private DatabaseHelper dbHelper;
    private RecipeDAO recipeDAO;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        initializeViews();
        initializeDatabase();
        loadRecipeDetails();
        setupClickListeners();
    }

    private void initializeViews() {
        recipeName = findViewById(R.id.recipeName);
        recipeDescription = findViewById(R.id.recipeDescription);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeInstructions = findViewById(R.id.recipeInstructions);
        recipeNutrition = findViewById(R.id.recipeNutrition);
        addRecipeButton = findViewById(R.id.addRecipeButton);
        backButton = findViewById(R.id.backButton);
        recipeScrollView = findViewById(R.id.recipeScrollView);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
        recipeDAO = new RecipeDAO(dbHelper);
    }

    private void loadRecipeDetails() {
        String recipeId = getIntent().getStringExtra("recipe_id");
        if (recipeId == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            try {
                recipe = recipeDAO.getRecipeById(recipeId);
                runOnUiThread(() -> {
                    if (recipe != null) {
                        displayRecipeDetails();
                    } else {
                        Toast.makeText(RecipeDetailActivity.this, "Recipe not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(RecipeDetailActivity.this, "Error loading recipe", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void displayRecipeDetails() {
        recipeName.setText(recipe.getName());
        recipeDescription.setText(recipe.getDescription() != null ? recipe.getDescription() : "No description");
        recipeIngredients.setText(recipe.getIngredients() != null ? recipe.getIngredients() : "No ingredients");
        recipeInstructions.setText(recipe.getInstructions() != null ? recipe.getInstructions() : "No instructions");
        recipeNutrition.setText(recipe.getNutritionInfo() != null ? recipe.getNutritionInfo() : "No nutrition info");
        
        // Apply fade in animation to content
        AnimationUtils.applyFadeInAnimation(recipeScrollView);
    }

    private void setupClickListeners() {
        addRecipeButton.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(addRecipeButton);
            if (recipe != null) {
                try {
                    Intent intent = new Intent(this, AddRecipeActivity.class);
                    intent.putExtra("recipe_id", recipe.getRecipeId());
                    startActivity(intent);
                    AnimationUtils.applySlideInActivityTransition(this);
                } catch (Exception e) {
                    Toast.makeText(this, "Add recipe feature coming soon", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(v -> {
            AnimationUtils.applyRippleEffect(backButton);
            finish();
        });
    }
}
