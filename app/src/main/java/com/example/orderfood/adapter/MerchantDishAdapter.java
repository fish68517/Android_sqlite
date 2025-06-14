package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.R;
import com.example.orderfood.model.Dish;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MerchantDishAdapter extends RecyclerView.Adapter<MerchantDishAdapter.ViewHolder> {
    private Context context;
    private List<Dish> dishes;
    private OnItemClickListener listener;

    public MerchantDishAdapter(Context context, List<Dish> dishes) {
        this.context = context;
        this.dishes = dishes;
    }

    public interface OnItemClickListener {
        void onEditClick(Dish dish);
        void onDeleteClick(Dish dish);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_merchant_dish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dish dish = dishes.get(position);

        holder.tvName.setText(dish.getName());
        holder.tvPrice.setText(String.format("¥%.2f", dish.getPrice()));
        holder.tvDescription.setText(dish.getDescription());
        
        if (dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {



            if (dish.getImageUrl().startsWith("content://")) {
                System.out.println("imageResId ggg: " + dish.getImageUrl());
                String imageUrl =dish.getImageUrl();
                Glide.with(context)
                        .load(imageUrl)
                        .into(holder.ivDish);
            } else {
                // 设置商家图片
                int imageResId = context.getResources().getIdentifier(
                        dish.getImageUrl(), "mipmap",context.getPackageName());
                Glide.with(context)
                        .load(imageResId)
                        .placeholder(R.mipmap.jiushui_natie)
                        .error(R.mipmap.jiushui_natie)
                        .into(holder.ivDish);
            }


        } else {
            holder.ivDish.setImageResource(R.mipmap.jiushui_natie);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(dish);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(dish);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ivDish;
        TextView tvName;
        TextView tvPrice;
        TextView tvDescription;
        ImageButton btnEdit;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivDish = itemView.findViewById(R.id.ivDish);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnEdit = itemView.findViewById(R.id.editButton);
            btnDelete = itemView.findViewById(R.id.deleteButton);
        }
    }
} 