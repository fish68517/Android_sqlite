package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.model.DishCategory;
import com.example.orderfood.R;

import java.util.List;

public class CategoryIconAdapter extends RecyclerView.Adapter<CategoryIconAdapter.CategoryViewHolder> {
    private List<DishCategory> categories;
    private Context context;
    private OnItemClickListener listener;

    public void setCategories(List<DishCategory> categories) {
        this.categories = categories;

    }

    public interface OnItemClickListener {
        void onItemClick(DishCategory category);
    }

    public CategoryIconAdapter(Context context, List<DishCategory> categories) {
        this.context = context;
        this.categories = categories;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        DishCategory category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView categoryIcon;
        private TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            categoryName = itemView.findViewById(R.id.categoryName);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(categories.get(getAdapterPosition()));
                }
            });
        }

        public void bind(DishCategory category) {
            categoryName.setText(category.getName());
            // 使用Glide加载图标
            Glide.with(context)
                .load(category.getImageUrl())
                .into(categoryIcon);
        }
    }
} 