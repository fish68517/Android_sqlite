package com.example.healthdietapp.fragments;

import android.content.Intent;
import android.os.Bundle;
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
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    }

    private void setupRecyclerViews() {
        // Main categories (vertical list on left)
        LinearLayoutManager mainCategoriesLayoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        mainCategoriesRecyclerView.setLayoutManager(mainCategoriesLayoutManager);
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), (category, position) -> {
            selectedCategoryId = category.getCategoryId();
            categoryAdapter.setSelectedPosition(position);
            loadSubcategoriesAndRecipes(category.getCategoryId());
        });
        mainCategoriesRecyclerView.setAdapter(categoryAdapter);

        // Subcategories (horizontal list)
        LinearLayoutManager subcategoriesLayoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        subcategoriesRecyclerView.setLayoutManager(subcategoriesLayoutManager);
        subcategoryAdapter = new SubcategoryAdapter(new ArrayList<>(), subcategory -> {
            loadRecipesByCategory(subcategory.getCategoryId());
        });
        subcategoriesRecyclerView.setAdapter(subcategoryAdapter);

        // Recipes (vertical list)
        LinearLayoutManager recipesLayoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        recipesRecyclerView.setLayoutManager(recipesLayoutManager);
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getRecipeId());
            startActivity(intent);
        });
        recipesRecyclerView.setAdapter(recipeAdapter);

        // Search history (vertical list)
        LinearLayoutManager searchHistoryLayoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        searchHistoryRecyclerView.setLayoutManager(searchHistoryLayoutManager);
        searchHistoryAdapter = new SearchHistoryAdapter(new ArrayList<>(), keyword -> {
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
        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        // Record search history
        new Thread(() -> {
            try {
                SearchHistory history = new SearchHistory();
                history.setHistoryId(UUID.randomUUID().toString());
                history.setUserId(userId);
                history.setKeyword(keyword);
                history.setSearchedAt(System.currentTimeMillis());
                searchHistoryDAO.addSearchHistory(history);

                // Search recipes
                List<Recipe> searchResults = recipeDAO.searchRecipes(keyword);
                requireActivity().runOnUiThread(() -> {
                    recipeAdapter.updateRecipes(searchResults);
                    if (searchResults.isEmpty()) {
                        Toast.makeText(requireContext(), "未找到相关食谱", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "搜索失败，请重试", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void loadMainCategories() {
        new Thread(() -> {
            try {
                List<RecipeCategory> mainCategories = recipeDAO.getMainCategories();
                requireActivity().runOnUiThread(() -> {
                    categoryAdapter.updateCategories(mainCategories);
                    if (!mainCategories.isEmpty()) {
                        // Load first category by default
                        selectedCategoryId = mainCategories.get(0).getCategoryId();
                        categoryAdapter.setSelectedPosition(0);
                        loadSubcategoriesAndRecipes(selectedCategoryId);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadSubcategoriesAndRecipes(String parentCategoryId) {
        new Thread(() -> {
            try {
                List<RecipeCategory> subcategories = recipeDAO.getSubcategories(parentCategoryId);
                List<Recipe> recipes = recipeDAO.getRecipesByCategory(parentCategoryId);

                requireActivity().runOnUiThread(() -> {
                    subcategoryAdapter.updateSubcategories(subcategories);
                    recipeAdapter.updateRecipes(recipes);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadRecipesByCategory(String categoryId) {
        new Thread(() -> {
            try {
                List<Recipe> recipes = recipeDAO.getRecipesByCategory(categoryId);
                requireActivity().runOnUiThread(() -> {
                    recipeAdapter.updateRecipes(recipes);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadSearchHistory() {
        new Thread(() -> {
            try {
                List<String> keywords = searchHistoryDAO.getUniqueSearchKeywords(userId);
                requireActivity().runOnUiThread(() -> {
                    if (keywords.isEmpty()) {
                        searchHistoryContainer.setVisibility(View.GONE);
                    } else {
                        searchHistoryContainer.setVisibility(View.VISIBLE);
                        searchHistoryAdapter.updateKeywords(keywords);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void clearSearchHistory() {
        new Thread(() -> {
            try {
                boolean success = searchHistoryDAO.deleteAllUserSearchHistory(userId);
                requireActivity().runOnUiThread(() -> {
                    if (success) {
                        searchHistoryContainer.setVisibility(View.GONE);
                        searchHistoryAdapter.updateKeywords(new ArrayList<>());
                        Toast.makeText(requireContext(), "搜索历史已清空", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "清空历史失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "清空历史失败，请重试", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}

