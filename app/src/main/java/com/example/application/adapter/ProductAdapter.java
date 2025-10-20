package com.example.application.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.application.activity.ProductDetailActivity;
import com.example.application.databinding.ItemProductBinding;
import com.example.application.model.Product;

import java.util.Locale;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {

    public ProductAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getPrice() == newItem.getPrice();
        }
    };

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = getItem(position);
        holder.bind(currentProduct);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        public ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // 任务: 详细信息屏幕 - 级别 2
            // 描述: 设置点击监听器，当用户点击商品卡片时，携带商品数据跳转到详情页。
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Product product = getItem(position);
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("PRODUCT_EXTRA", product);
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Product product) {
            binding.productName.setText(product.getName());
            binding.productPrice.setText(String.format(Locale.getDefault(), "¥ %.2f", product.getPrice()));

            // 任务: 带图片的列表 (RecyclerView with Glide) - 级别 2
            // 描述: 使用Glide库从URL加载图片并显示在ImageView中。
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .into(binding.productImage);
        }
    }
}
