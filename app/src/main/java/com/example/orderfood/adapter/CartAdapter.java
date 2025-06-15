package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.R;

import com.example.orderfood.activity.CartActivity;
import com.example.orderfood.model.CartItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private Map<Integer, Boolean> selectedItems; // 记录选中状态

    public CartAdapter(Context context ) {
        this.context = context;

        this.cartItems = new ArrayList<>();
        this.selectedItems = new HashMap<>();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_dish, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    public void setAllItemsSelected(boolean selected) {
        for (CartItem item : cartItems) {
            selectedItems.put(item.getRecordId(), selected);
        }
        notifyDataSetChanged();
    }

    public List<CartItem> getSelectedItems() {
        List<CartItem> selected = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (selectedItems.get(item.getRecordId()) == Boolean.TRUE) {
                selected.add(item);
            }
        }
        return selected;
    }

    public double getSelectedItemsTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (selectedItems.get(item.getRecordId()) == Boolean.TRUE) {
                total += item.getSubtotal();
            }
        }
        return total;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private CheckBox itemCheckbox;
        private ImageView dishImage;
        private TextView dishName;
        private TextView dishPrice;
        private TextView quantityText;
        private ImageButton decreaseButton;
        private ImageButton increaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCheckbox = itemView.findViewById(R.id.itemCheckbox);
            dishImage = itemView.findViewById(R.id.dishImage);
            dishName = itemView.findViewById(R.id.dishName);
            dishPrice = itemView.findViewById(R.id.dishPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
        }

        public void bind(CartItem item) {
            dishName.setText(item.getDishName());
            dishPrice.setText(String.format("¥%.2f", item.getPrice()));
            quantityText.setText(String.valueOf(item.getQuantity()));

            // 设置商家图片
            System.out.println("imageUrl: " + item.getImageUrl());
            int imageResId = context.getResources().getIdentifier(
                    item.getImageUrl(), "mipmap",context.getPackageName());
            System.out.println("imageResId: " + imageResId);
            dishImage.setImageResource(imageResId);
            // 设置选中状态
            itemCheckbox.setChecked(selectedItems.get(item.getRecordId()) == Boolean.TRUE);
            itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                selectedItems.put(item.getRecordId(), isChecked);
                // 通知Fragment更新总价
                if (context instanceof CartActivity) {
                    ((CartActivity) context).updateTotalPrice();
                }
            });

            // 设置加减按钮
            setupQuantityButtons(item);
        }

        private void setupQuantityButtons(CartItem item) {
            decreaseButton.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    updateItemQuantity(item, item.getQuantity() - 1);
                }
            });

            increaseButton.setOnClickListener(v -> {
                updateItemQuantity(item, item.getQuantity() + 1);
            });
        }

        private void updateItemQuantity(CartItem item, int newQuantity) {
            DBMysqlHelper.getInstance(context).updateCartItemQuantity(
                item.getRecordId(),
                newQuantity,
                new DBMysqlHelper.DatabaseCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        item.setQuantity(newQuantity);
                        quantityText.setText(String.valueOf(newQuantity));
                        // 通知Fragment更新总价
                        // 通知Fragment更新总价
                        if (context instanceof CartActivity) {
                            ((CartActivity) context).updateTotalPrice();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, "更新数量失败", Toast.LENGTH_SHORT).show();
                    }
                }
            );
        }
    }
} 