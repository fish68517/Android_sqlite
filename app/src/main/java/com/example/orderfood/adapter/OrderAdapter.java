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
import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.R;
import com.example.orderfood.model.Dish;
import com.example.orderfood.model.Orders;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<Orders> orders;
    private OnPayClickListener payClickListener;
    private DBMysqlHelper dbHelper;

    public interface OnPayClickListener {
        void onPayClick(Orders order);
    }

    public OrderAdapter(Context context, List<Orders> orders) {
        this.context = context;
        this.orders = orders;
        this.dbHelper = DBMysqlHelper.getInstance();
    }

    public void setOnPayClickListener(OnPayClickListener listener) {
        this.payClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Orders order = orders.get(position);
        
        // 设置订单状态
        holder.statusChip.setText(getStatusText(order.getOrderStatus()));
        holder.statusChip.setChipBackgroundColorResource(getStatusColor(order.getOrderStatus()));
        
        // 设置配送方式
        holder.deliveryChip.setText(order.getDiningOption().equals("delivery") ? "外卖配送" : "到店自取");
        
        // 设置订单总价
        holder.priceText.setText(String.format("¥%.2f", order.getTotalPrice()));
        
        // 设置订单时间
        holder.timeText.setText(order.getOrderTime());

        // 解析并显示菜品列表
        String[] dishItems = order.getDishList().split(",");
        StringBuilder dishText = new StringBuilder();
        for (String item : dishItems) {
            String[] parts = item.split(":");
            int dishId = Integer.parseInt(parts[0]);
            int quantity = Integer.parseInt(parts[1]);
            
            // 获取菜品信息
            dbHelper.getDishInfo(dishId, new DBMysqlHelper.DatabaseCallback<Dish>() {
                @Override
                public void onSuccess(Dish dish) {
                    if (dish != null) {
                        // 加载菜品图片
                        System.out.println("dish image url: " + dish.getImageUrl());

                        // 设置商家图片

                        if (dish.getImageUrl().startsWith("content://")) {
                            System.out.println("imageResId ggg: " + dish.getImageUrl());
                            String imageUrl =dish.getImageUrl();
                            Glide.with(context)
                                    .load(imageUrl)
                                    .into(holder.dishImage);
                        } else {
                            int imageResId = context.getResources().getIdentifier(
                                    dish.getImageUrl(), "mipmap", context.getPackageName());
                            Glide.with(context)
                                    .load(imageResId)
                                    .into(holder.dishImage);
                        }


                        
                        // 设置菜品名称和数量
                        holder.dishName.setText(dish.getName());
                        holder.quantityText.setText(String.format("x%d", quantity));
                    }
                }

                @Override
                public void onError(Exception e) {
                    // 处理错误
                }
            });
        }

        // 设置支付按钮状态
        holder.payButton.setVisibility(order.getOrderStatus().equals("pending") ? View.VISIBLE : View.GONE);
        holder.payButton.setOnClickListener(v -> {
            if (payClickListener != null) {
                payClickListener.onPayClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "待支付";
            case "paid": return "已支付";
            case "completed": return "已完成";
            case "cancelled": return "已取消";
            default: return status;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "pending": return R.color.status_pending;
            case "paid": return R.color.status_paid;
            case "completed": return R.color.status_completed;
            case "cancelled": return R.color.status_cancelled;
            default: return R.color.status_pending;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        Chip statusChip;
        Chip deliveryChip;
        TextView timeText;
        ImageView dishImage;
        TextView dishName;
        TextView quantityText;
        TextView priceText;
        MaterialButton payButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            statusChip = itemView.findViewById(R.id.statusChip);
            deliveryChip = itemView.findViewById(R.id.deliveryChip);
            timeText = itemView.findViewById(R.id.timeText);
            dishImage = itemView.findViewById(R.id.dishImage);
            dishName = itemView.findViewById(R.id.dishName);
            quantityText = itemView.findViewById(R.id.quantityText);
            priceText = itemView.findViewById(R.id.priceText);
            payButton = itemView.findViewById(R.id.payButton);
        }
    }
} 