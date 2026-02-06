package com.example.healthdietapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.models.RecipeCategory;
import com.example.healthdietapp.utils.AnimationUtils;

import java.util.List;

/**
 * CategoryAdapter - Adapter for displaying recipe categories in RecyclerView
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<RecipeCategory> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = -1;

    public interface OnCategoryClickListener {
        void onCategoryClick(RecipeCategory category, int position);
    }

    public CategoryAdapter(List<RecipeCategory> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        RecipeCategory category = categories.get(position);
        holder.bind(category, listener, position, selectedPosition);
        
        // Apply list item enter animation with staggered delay
        AnimationUtils.applyListItemEnterAnimationWithDelay(holder.itemView, position * 50);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateCategories(List<RecipeCategory> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
        notifyItemChanged(position);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
        }

        void bind(RecipeCategory category, OnCategoryClickListener listener, int position, int selectedPosition) {
            categoryName.setText(category.getName());
            
            // Highlight selected category
            if (position == selectedPosition) {
                categoryName.setTextColor(itemView.getContext().getColor(R.color.md_theme_light_primary));
                categoryName.setTextStyle(android.graphics.Typeface.BOLD);
            } else {
                categoryName.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                categoryName.setTextStyle(android.graphics.Typeface.NORMAL);
            }
            
            itemView.setOnClickListener(v -> listener.onCategoryClick(category, position));
        }
    }
}
