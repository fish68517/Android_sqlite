package com.archive.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.archive.app.model.Category;
import com.example.myapplication.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
        void onDeleteClick(Category category);
    }

    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.name.setText(category.getName());
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategories(List<Category> categories) {
        this.categoryList = categories;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            deleteButton = itemView.findViewById(R.id.delete_category);
        }
    }
} 