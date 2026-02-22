package com.example.healthdietapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.activities.RecipeDetailActivity;
import com.example.healthdietapp.adapters.CategoryAdapter;
import com.example.healthdietapp.adapters.RecipeAdapter;
import com.example.healthdietapp.adapters.SearchHistoryAdapter;
import com.example.healthdietapp.adapters.SubcategoryAdapter;
import com.example.healthdietapp.database.DatabaseHelper;
import com.example.healthdietapp.database.RecipeDAO;
import com.example.healthdietapp.database.SearchHistoryDAO;
import com.example.healthdietapp.models.Recipe;
import com.example.healthdietapp.models.RecipeCategory;
import com.example.healthdietapp.models.SearchHistory;
import com.example.healthdietapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Category Fragment - Displays recipe categories and search functionality
 */
public class CategoryFragment extends Fragment {

    private static final String TAG = "CategoryFragment"; // 增加日志 TAG

    private EditText searchBar;
    private Button searchButton;
    private Button clearHistoryButton;
    private RecyclerView mainCategoriesRecyclerView;
    private RecyclerView subcategoriesRecyclerView;
    private RecyclerView recipesRecyclerView;
    private RecyclerView searchHistoryRecyclerView;
    private LinearLayout searchHistoryContainer;

    private DatabaseHelper dbHelper;
    private RecipeDAO recipeDAO;
    private SearchHistoryDAO searchHistoryDAO;
    private SessionManager sessionManager;

    private CategoryAdapter categoryAdapter;
    private SubcategoryAdapter subcategoryAdapter;
    private RecipeAdapter recipeAdapter;
    private SearchHistoryAdapter searchHistoryAdapter;

    private String userId;
    private String selectedCategoryId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "==== onCreateView: 初始化布局 ====");
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "==== onViewCreated: 绑定视图与数据 ====");
        initializeViews(view);
        initializeDatabase();
        setupRecyclerViews();
        setupSearchFunctionality();
        loadMainCategories();
    }

    private void initializeViews(View view) {
        searchBar = view.findViewById(R.id.searchBar);
        searchButton = view.findViewById(R.id.searchButton);
        clearHistoryButton = view.findViewById(R.id.clearHistoryButton);
        mainCategoriesRecyclerView = view.findViewById(R.id.mainCategoriesRecyclerView);
        subcategoriesRecyclerView = view.findViewById(R.id.subcategoriesRecyclerView);
        recipesRecyclerView = view.findViewById(R.id.recipesRecyclerView);
        searchHistoryRecyclerView = view.findViewById(R.id.searchHistoryRecyclerView);
        searchHistoryContainer = view.findViewById(R.id.searchHistoryContainer);
    }

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(requireContext());
        recipeDAO = new RecipeDAO(dbHelper);
        searchHistoryDAO = new SearchHistoryDAO(dbHelper);
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();
        Log.d(TAG, "数据库初始化完成, 当前用户 ID: " + userId);
    }

    private void setupRecyclerViews() {
        // Main categories (vertical list on left)
        LinearLayoutManager mainCategoriesLayoutManager = new LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false);
        mainCategoriesRecyclerView.setLayoutManager(mainCategoriesLayoutManager);
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), (category, position) -> {
            selectedCategoryId = category.getCategoryId();
            Log.d(TAG, "点击主分类: " + category.getName() + " (ID: " + selectedCategoryId + ")");
            categoryAdapter.setSelectedPosition(position);
            loadSubcategoriesAndRecipes(selectedCategoryId);
        });
        mainCategoriesRecyclerView.setAdapter(categoryAdapter);

        // Subcategories (horizontal list)
        LinearLayoutManager subcategoriesLayoutManager = new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false);
        subcategoriesRecyclerView.setLayoutManager(subcategoriesLayoutManager);
        subcategoryAdapter = new SubcategoryAdapter(new ArrayList<>(), subcategory -> {
            Log.d(TAG, "点击子分类: " + subcategory.getName());
            loadRecipesByCategory(subcategory.getCategoryId());
        });
        subcategoriesRecyclerView.setAdapter(subcategoryAdapter);

        // Recipes (vertical list)
        LinearLayoutManager recipesLayoutManager = new LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false);
        recipesRecyclerView.setLayoutManager(recipesLayoutManager);
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            Log.d(TAG, "点击食谱进入详情: " + recipe.getName());
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getRecipeId());
            startActivity(intent);
        });
        recipesRecyclerView.setAdapter(recipeAdapter);

        // Search history (vertical list)
        LinearLayoutManager searchHistoryLayoutManager = new LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false);
        searchHistoryRecyclerView.setLayoutManager(searchHistoryLayoutManager);
        searchHistoryAdapter = new SearchHistoryAdapter(new ArrayList<>(), keyword -> {
            Log.d(TAG, "点击搜索历史关键词: " + keyword);
            searchBar.setText(keyword);
            performSearch();
        });
        searchHistoryRecyclerView.setAdapter(searchHistoryAdapter);
    }

    private void setupSearchFunctionality() {
        searchButton.setOnClickListener(v -> performSearch());
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
        clearHistoryButton.setOnClickListener(v -> clearSearchHistory());

        // Load and display search history on fragment load
        loadSearchHistory();
    }

    private void performSearch() {
        String keyword = searchBar.getText().toString().trim();
        Log.d(TAG, "执行搜索, 关键词: [" + keyword + "]");

        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (userId != null) {
                SearchHistory history = new SearchHistory();
                history.setHistoryId(UUID.randomUUID().toString());
                history.setUserId(userId);
                history.setKeyword(keyword);
                history.setSearchedAt(System.currentTimeMillis());
                searchHistoryDAO.addSearchHistory(history);
                Log.d(TAG, "搜索历史已存入数据库");
            }

            // Search recipes
            List<Recipe> searchResults = recipeDAO.searchRecipes(keyword);

            // 安全地更新UI
            if (searchResults != null) {
                Log.d(TAG, "搜索完成, 找到 " + searchResults.size() + " 条结果");
                recipeAdapter.updateRecipes(searchResults);
                if (searchResults.isEmpty()) {
                    Toast.makeText(requireContext(), "未找到相关食谱", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "搜索返回 null");
                recipeAdapter.updateRecipes(new ArrayList<>());
                Toast.makeText(requireContext(), "未找到相关食谱", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "执行搜索时发生异常", e);
            Toast.makeText(requireContext(), "搜索失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMainCategories() {
        Log.d(TAG, "开始加载主分类数据");
        try {
            List<RecipeCategory> mainCategories = recipeDAO.getMainCategories();

            // 安全地更新UI
            if (mainCategories != null) {
                Log.d(TAG, "加载主分类成功, 共 " + mainCategories.size() + " 项");
                categoryAdapter.updateCategories(mainCategories);
                if (!mainCategories.isEmpty()) {
                    // Load first category by default
                    selectedCategoryId = mainCategories.get(0).getCategoryId();
                    Log.d(TAG, "默认选中第一个主分类: " + mainCategories.get(0).getName());
                    categoryAdapter.setSelectedPosition(0);
                    loadSubcategoriesAndRecipes(selectedCategoryId);
                }
            } else {
                Log.w(TAG, "主分类数据为 null");
                categoryAdapter.updateCategories(new ArrayList<>());
            }
        } catch (Exception e) {
            Log.e(TAG, "加载主分类时发生异常", e);
        }
    }

    private void loadSubcategoriesAndRecipes(String parentCategoryId) {
        Log.d(TAG, "开始加载子分类及对应的食谱, 父分类ID: " + parentCategoryId);
        try {
            List<RecipeCategory> subcategories = recipeDAO.getSubcategories(parentCategoryId);
            List<Recipe> recipes = recipeDAO.getRecipesByCategory(parentCategoryId);

            if (subcategories != null) {
                Log.d(TAG, "加载子分类成功, 共 " + subcategories.size() + " 项");
                subcategoryAdapter.updateSubcategories(subcategories);
            }
            if (recipes != null) {
                Log.d(TAG, "加载食谱成功, 共 " + recipes.size() + " 项");
                recipeAdapter.updateRecipes(recipes);
            }
        } catch (Exception e) {
            Log.e(TAG, "加载子分类或食谱时发生异常", e);
        }
    }

    private void loadRecipesByCategory(String categoryId) {
        Log.d(TAG, "根据子分类ID筛选食谱: " + categoryId);
        try {
            List<Recipe> recipes = recipeDAO.getRecipesByCategory(categoryId);

            // 安全地更新UI
            if (recipes != null) {
                Log.d(TAG, "筛选食谱成功, 找到 " + recipes.size() + " 项");
                recipeAdapter.updateRecipes(recipes);
            }
        } catch (Exception e) {
            Log.e(TAG, "筛选食谱时发生异常", e);
        }
    }

    private void loadSearchHistory() {
        Log.d(TAG, "准备加载搜索历史");
        try {
            if (userId == null) {
                Log.w(TAG, "userId 为 null, 无法加载搜索历史");
                return;
            }
            List<String> keywords = searchHistoryDAO.getUniqueSearchKeywords(userId);

            // 安全地更新UI
            if (keywords == null || keywords.isEmpty()) {
                Log.d(TAG, "没有搜索历史记录，隐藏历史面板");
                searchHistoryContainer.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "加载搜索历史成功, 共 " + keywords.size() + " 条");
                searchHistoryContainer.setVisibility(View.VISIBLE);
                searchHistoryAdapter.updateKeywords(keywords);
            }
        } catch (Exception e) {
            Log.e(TAG, "加载搜索历史时发生异常", e);
        }
    }

    private void clearSearchHistory() {
        Log.d(TAG, "准备清空搜索历史");
        try {
            if (userId == null) return;
            boolean success = searchHistoryDAO.deleteAllUserSearchHistory(userId);

            // 安全地更新UI
            if (success) {
                Log.d(TAG, "搜索历史已成功清空");
                searchHistoryContainer.setVisibility(View.GONE);
                searchHistoryAdapter.updateKeywords(new ArrayList<>());
                Toast.makeText(requireContext(), "搜索历史已清空", Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "清空搜索历史失败(数据库返回 false)");
                Toast.makeText(requireContext(), "清空历史失败，请重试", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "清空搜索历史时发生异常", e);
            Toast.makeText(requireContext(), "清空历史失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}