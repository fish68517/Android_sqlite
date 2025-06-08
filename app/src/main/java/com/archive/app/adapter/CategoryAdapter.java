package com.archive.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.model.Category;
import com.example.myapplication.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private final OnCategoryListener listener;

    public interface OnCategoryListener {
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    public CategoryAdapter(List<Category> categoryList, OnCategoryListener listener) {
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
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
    }

    public void setCategories(List<Category> categories) {
        this.categoryList = categories;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryName;
        private final Button editButton;
        private final Button deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.tv_category_name);
            editButton = itemView.findViewById(R.id.btn_edit_category);
            deleteButton = itemView.findViewById(R.id.btn_delete_category);
        }

        public void bind(final Category category, final OnCategoryListener listener) {
            categoryName.setText(category.getName());
            editButton.setOnClickListener(v -> listener.onEditClick(category));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(category));
        }
    }
} 