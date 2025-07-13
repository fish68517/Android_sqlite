package com.example.xiaoshuo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0; // 默认选中第一个

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }
    
    public OnCategoryClickListener getOnCategoryClickListener() {
        return listener;
    }
    
    public int getSelectedPosition() {
        return selectedPosition;
    }
    
    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategory.setText(category.getName());
        
        // 设置选中状态
        if (selectedPosition == position) {
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.primary));
            holder.itemView.setBackgroundResource(R.drawable.bg_category_selected);
        } else {
            holder.tvCategory.setTextColor(context.getResources().getColor(R.color.text_primary));
            holder.itemView.setBackgroundResource(R.drawable.bg_category_normal);
        }

        holder.itemView.setOnClickListener(v -> {
            // 更新选中位置
            int oldSelectedPosition = selectedPosition;
            selectedPosition = position;
            
            // 刷新前后两项的视图状态
            if (oldSelectedPosition != -1) {
                notifyItemChanged(oldSelectedPosition);
            }
            notifyItemChanged(selectedPosition);
            
            if (listener != null) {
                listener.onCategoryClick(category, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
        }
    }
} 