package com.example.healthdietapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.models.Recipe;
import com.example.healthdietapp.utils.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * CollectionRecipeAdapter - Adapter for displaying collected recipes
 */
public class CollectionRecipeAdapter extends RecyclerView.Adapter<CollectionRecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public CollectionRecipeAdapter(OnRecipeClickListener listener) {
        this.recipes = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, listener);
        
        // Apply list item enter animation with staggered delay
        AnimationUtils.applyListItemEnterAnimationWithDelay(holder.itemView, position * 50);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void updateRecipes(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
        notifyDataSetChanged();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeName;
        private TextView recipeDescription;
        private TextView recipeCategory;
        private ImageView recipeImage;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeDescription = itemView.findViewById(R.id.recipeDescription);
            recipeCategory = itemView.findViewById(R.id.recipeCategory);
            recipeImage = itemView.findViewById(R.id.recipeImage);
        }

        void bind(Recipe recipe, OnRecipeClickListener listener) {
            recipeName.setText(recipe.getName());
            recipeDescription.setText(recipe.getDescription());
            recipeCategory.setText(recipe.getCategory());
            itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
        }
    }
}
