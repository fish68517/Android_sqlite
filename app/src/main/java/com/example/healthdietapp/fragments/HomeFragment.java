package com.example.healthdietapp.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.activities.HealthRecordActivity;
import com.example.healthdietapp.activities.RecipeDetailActivity;
import com.example.healthdietapp.adapters.RecipeAdapter;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.RecipeDAO;
import com.example.healthdietapp.database.UserRecipeDAO;
import com.example.healthdietapp.models.Recipe;
import com.example.healthdietapp.models.UserRecipe;
import com.example.healthdietapp.utils.DateUtils;
import com.example.healthdietapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Home Fragment - Displays user's daily recipes and recommendations
 */
public class HomeFragment extends Fragment {

    private MaterialButton prevDateButton;
    private MaterialButton nextDateButton;
    // private MaterialButton datePickerButton;
    // private Button dailyRecordButton;
    private MaterialButton moreRecommendedButton;
    private MaterialCardView breakfastCard;
    private MaterialCardView lunchCard;
    private MaterialCardView dinnerCard;
    private TextView breakfastRecipeName;
    private TextView lunchRecipeName;
    private TextView dinnerRecipeName;
    private RecyclerView recommendedRecipesRecyclerView;

    private DatabaseHelper dbHelper;
    private UserRecipeDAO userRecipeDAO;
    private RecipeDAO recipeDAO;
    private SessionManager sessionManager;
    private RecipeAdapter recipeAdapter;

    private String currentDate;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        initializeDatabase();
        initializeCurrentDate();
        setupDateNavigation();
        setupRecyclerView();
        loadRecipes();
    }

    private void initializeViews(View view) {
        prevDateButton = view.findViewById(R.id.prevDateButton);
        nextDateButton = view.findViewById(R.id.nextDateButton);
      //  datePickerButton = view.findViewById(R.id.datePickerButton);
       // dailyRecordButton = view.findViewById(R.id.dailyRecordButton);
        moreRecommendedButton = view.findViewById(R.id.moreRecommendedButton);
        breakfastCard = view.findViewById(R.id.breakfastCard);
        lunchCard = view.findViewById(R.id.lunchCard);
        dinnerCard = view.findViewById(R.id.dinnerCard);
        breakfastRecipeName = view.findViewById(R.id.breakfastRecipeName);
        lunchRecipeName = view.findViewById(R.id.lunchRecipeName);
        dinnerRecipeName = view.findViewById(R.id.dinnerRecipeName);
        recommendedRecipesRecyclerView = view.findViewById(R.id.recommendedRecipesRecyclerView);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(requireContext());
        userRecipeDAO = new UserRecipeDAO(dbHelper);
        recipeDAO = new RecipeDAO(dbHelper);
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();
    }

    private void initializeCurrentDate() {
        currentDate = DateUtils.getCurrentDate();
        updateDateDisplay();
    }

    private void setupDateNavigation() {
        prevDateButton.setOnClickListener(v -> {
            currentDate = DateUtils.addDays(currentDate, -1);
            updateDateDisplay();
            loadRecipes();
        });

        nextDateButton.setOnClickListener(v -> {
            currentDate = DateUtils.addDays(currentDate, 1);
            updateDateDisplay();
            loadRecipes();
        });

        /*datePickerButton.setOnClickListener(v -> showDatePicker());*/

//        dailyRecordButton.setOnClickListener(v -> {
//            Intent intent = new Intent(requireContext(), HealthRecordActivity.class);
//            startActivity(intent);
//        });

        moreRecommendedButton.setOnClickListener(v -> {
            // Navigate to CategoryFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CategoryFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void updateDateDisplay() {
        String displayDate = DateUtils.getDisplayDate(currentDate);
        // datePickerButton.setText(displayDate);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse(currentDate);
            calendar.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    currentDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    updateDateDisplay();
                    loadRecipes();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recommendedRecipesRecyclerView.setLayoutManager(layoutManager);
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getRecipeId());
            startActivity(intent);
        });
        recommendedRecipesRecyclerView.setAdapter(recipeAdapter);
    }

    private void loadRecipes() {
        loadUserRecipes();
        loadRecommendedRecipes();
    }

    private void loadUserRecipes() {

            try {
                List<UserRecipe> userRecipes = userRecipeDAO.getUserRecipesByDate(userId, currentDate);
                
                Recipe breakfastRecipe = null;
                Recipe lunchRecipe = null;
                Recipe dinnerRecipe = null;

                for (UserRecipe userRecipe : userRecipes) {
                    Recipe recipe = recipeDAO.getRecipeById(userRecipe.getRecipeId());
                    if (recipe != null) {
                        switch (userRecipe.getMealType()) {
                            case "breakfast":
                                breakfastRecipe = recipe;
                                break;
                            case "lunch":
                                lunchRecipe = recipe;
                                break;
                            case "dinner":
                                dinnerRecipe = recipe;
                                break;
                        }
                    }
                }

                Recipe finalBreakfastRecipe = breakfastRecipe;
                Recipe finalLunchRecipe = lunchRecipe;
                Recipe finalDinnerRecipe = dinnerRecipe;
                updateMealCard(breakfastRecipeName, finalBreakfastRecipe);
                updateMealCard(lunchRecipeName, finalLunchRecipe);
                updateMealCard(dinnerRecipeName, finalDinnerRecipe);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    private void updateMealCard(TextView mealTextView, Recipe recipe) {
        if (recipe != null) {
            mealTextView.setText(recipe.getName());
            mealTextView.setTextColor(requireContext().getColor(android.R.color.black));
        } else {
            mealTextView.setText("No recipe scheduled");
            mealTextView.setTextColor(requireContext().getColor(android.R.color.darker_gray));
        }
    }

    private void loadRecommendedRecipes() {
        List<Recipe> recommendedRecipes = recipeDAO.getRecommendedRecipes(10);
        recipeAdapter.updateRecipes(recommendedRecipes);
    }
}
