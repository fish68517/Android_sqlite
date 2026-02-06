package com.example.healthdietapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.models.RecipeCategory;

import java.util.List;

/**
 * SubcategoryAdapter - Adapter for displaying recipe subcategories in RecyclerView
 */
public class SubcategoryAdapter extends RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder> {

    private List<RecipeCategory> subcategories;
    private OnSubcategoryClickListener listener;

    public interface OnSubcategoryClickListener {
        void onSubcategoryClick(RecipeCategory subcategory);
    }

    public SubcategoryAdapter(List<RecipeCategory> subcategories, OnSubcategoryClickListener listener) {
        this.subcategories = subcategories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubcategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subcategory, parent, false);
        return new SubcategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoryViewHolder holder, int position) {
        RecipeCategory subcategory = subcategories.get(position);
        holder.bind(subcategory, listener);
    }

    @Override
    public int getItemCount() {
        return subcategories.size();
    }

    public void updateSubcategories(List<RecipeCategory> newSubcategories) {
        this.subcategories = newSubcategories;
        notifyDataSetChanged();
    }

    static class SubcategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView subcategoryName;

        SubcategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            subcategoryName = itemView.findViewById(R.id.subcategoryName);
        }

        void bind(RecipeCategory subcategory, OnSubcategoryClickListener listener) {
            subcategoryName.setText(subcategory.getName());
            itemView.setOnClickListener(v -> listener.onSubcategoryClick(subcategory));
        }
    }
}
