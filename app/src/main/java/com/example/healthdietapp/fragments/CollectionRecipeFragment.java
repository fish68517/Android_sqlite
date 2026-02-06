package com.example.healthdietapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.activities.RecipeDetailActivity;
import com.example.healthdietapp.adapters.CollectionRecipeAdapter;
import com.example.healthdietapp.database.RecipeDAO;
import com.example.healthdietapp.models.Recipe;

import java.util.List;

/**
 * CollectionRecipeFragment - Displays user's collected recipes
 */
public class CollectionRecipeFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_RECIPE_DAO = "recipeDAO";

    private String userId;
    private RecipeDAO recipeDAO;
    private RecyclerView recipesRecyclerView;
    private CollectionRecipeAdapter adapter;

    public static CollectionRecipeFragment newInstance(String userId, RecipeDAO recipeDAO) {
        CollectionRecipeFragment fragment = new CollectionRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        fragment.recipeDAO = recipeDAO;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipesRecyclerView = view.findViewById(R.id.recipesRecyclerView);
        setupRecyclerView();
        loadCollectedRecipes();
    }

    private void setupRecyclerView() {
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CollectionRecipeAdapter(recipe -> onRecipeClick(recipe));
        recipesRecyclerView.setAdapter(adapter);
    }

    private void loadCollectedRecipes() {
        new Thread(() -> {
            try {
                List<Recipe> recipes = recipeDAO.getUserCollectedRecipes(userId);
                getActivity().runOnUiThread(() -> {
                    if (recipes != null && !recipes.isEmpty()) {
                        adapter.updateRecipes(recipes);
                    } else {
                        Toast.makeText(getContext(), "No collected recipes", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to load recipes", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
        intent.putExtra("recipeId", recipe.getRecipeId());
        startActivity(intent);
    }
}
