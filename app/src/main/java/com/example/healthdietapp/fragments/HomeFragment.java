package com.example.healthdietapp.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
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
import java.util.UUID;

/**
 * Home Fragment - Displays user's daily recipes and recommendations
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private MaterialButton prevDateButton;
    private MaterialButton nextDateButton;
    private MaterialButton datePickerButton;
    private MaterialButton moreRecommendedButton;
    private MaterialButton saveMealsButton; // 新增：保存按钮

    private MaterialCardView breakfastCard;
    private MaterialCardView lunchCard;
    private MaterialCardView dinnerCard;

    // 改为 EditText，以支持用户输入
    private EditText breakfastRecipeName;
    private EditText lunchRecipeName;
    private EditText dinnerRecipeName;

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
        datePickerButton = view.findViewById(R.id.datePickerButton);
        moreRecommendedButton = view.findViewById(R.id.moreRecommendedButton);
        saveMealsButton = view.findViewById(R.id.saveMealsButton); // 绑定保存按钮

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

        datePickerButton.setOnClickListener(v -> showDatePicker());

        moreRecommendedButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CategoryFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // 绑定保存按钮事件
        saveMealsButton.setOnClickListener(v -> saveDailyMeals());
    }

    private void updateDateDisplay() {
        String displayDate = DateUtils.getDisplayDate(currentDate);
        if (datePickerButton != null) {
            datePickerButton.setText(displayDate);
        }
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
        new Thread(() -> {
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

                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        updateMealCard(breakfastRecipeName, finalBreakfastRecipe);
                        updateMealCard(lunchRecipeName, finalLunchRecipe);
                        updateMealCard(dinnerRecipeName, finalDinnerRecipe);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "加载用户食谱异常", e);
            }
        }).start();
    }

    // 修改为接收 EditText
    private void updateMealCard(EditText mealEditText, Recipe recipe) {
        if (recipe != null && recipe.getName() != null && !recipe.getName().isEmpty()) {
            mealEditText.setText(recipe.getName());
        } else {
            mealEditText.setText(""); // 清空，让 hint 提示语显示出来
        }
    }

    // 新增：保存三餐到数据库
    private void saveDailyMeals() {
        String breakfastText = breakfastRecipeName.getText().toString().trim();
        String lunchText = lunchRecipeName.getText().toString().trim();
        String dinnerText = dinnerRecipeName.getText().toString().trim();

        if (TextUtils.isEmpty(breakfastText) && TextUtils.isEmpty(lunchText) && TextUtils.isEmpty(dinnerText)) {
            Toast.makeText(requireContext(), "请至少输入一顿饭的食谱", Toast.LENGTH_SHORT).show();
            return;
        }
        saveMealsButton.setEnabled(false);
        saveMealsButton.setText("保存中...");

        try {
            saveSingleMeal("breakfast", breakfastText);
            saveSingleMeal("lunch", lunchText);
            saveSingleMeal("dinner", dinnerText);

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    saveMealsButton.setEnabled(true);
                    saveMealsButton.setText("保存今日食谱");
                    Toast.makeText(requireContext(), "食谱保存成功！", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "保存三餐异常", e);
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    saveMealsButton.setEnabled(true);
                    saveMealsButton.setText("保存今日食谱");
                    Toast.makeText(requireContext(), "保存失败，请重试", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    // 新增：处理单顿饭的保存逻辑
    private void saveSingleMeal(String mealType, String recipeName) {
        // 先删除该日期、该顿饭的历史记录（防止抛出 UNIQUE 约束异常）

        // 如果用户在输入框写了内容，才进行添加
        if (!recipeName.isEmpty()) {
            // 1. 生成一条自定义食谱放入 recipes 表
            // 先查询当天currentDate 是否存在食谱，如果存在则更新，不存在则插

            Recipe existingRecipe = recipeDAO.getRecipeByUserMeal(userId,currentDate,mealType);
            if (existingRecipe == null) {
                // 不存在则创建新食谱
                Recipe customRecipe = new Recipe();
                String newRecipeId = UUID.randomUUID().toString();
                customRecipe.setRecipeId(newRecipeId);
                customRecipe.setName(recipeName);
                customRecipe.setCategory("自定义输入");
                customRecipe.setCreatedBy(userId);
                customRecipe.setCreatedAt(System.currentTimeMillis());
                customRecipe.setUpdatedAt(System.currentTimeMillis());
                recipeDAO.createRecipe(customRecipe);
            } else {
                // 已存在则更新食谱名称和更新时间
                existingRecipe.setName(recipeName);
                existingRecipe.setUpdatedAt(System.currentTimeMillis());
                recipeDAO.updateRecipe(existingRecipe);
            }

            // 2. 将这条食谱关联到 user_recipes 安排表中
            UserRecipe userRecipe = new UserRecipe();
            userRecipe.setUserRecipeId(UUID.randomUUID().toString());
            userRecipe.setUserId(userId);
            userRecipe.setRecipeId(newRecipeId);
            userRecipe.setDate(currentDate);
            userRecipe.setMealType(mealType);
            userRecipe.setAddedAt(System.currentTimeMillis());


            userRecipeDAO.addUserRecipe(userRecipe);
            Log.d(TAG, "成功保存食谱: [" + mealType + "] " + recipeName);
        }
    }

    private void loadRecommendedRecipes() {
        try {
            List<Recipe> recommendedRecipes = recipeDAO.getRecommendedRecipes(10);
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    recipeAdapter.updateRecipes(recommendedRecipes);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}