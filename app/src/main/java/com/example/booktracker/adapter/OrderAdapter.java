package com.example.booktracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.booktracker.R;
import com.example.booktracker.entity.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<Order> list = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = list.get(position);
        
        // 加载图书封面
        Glide.with(holder.itemView).load(order.getBookUrl()).into(holder.ivBookCover);
        
        // 设置订单信息
        holder.tvBookName.setText(order.getBookName());
        holder.tvQuantity.setText("数量: " + order.getQuantity());
        holder.tvOrderTime.setText("下单时间: " + order.getCreateTime());
        
        // 设置订单状态
        String status;
        switch (order.getStatus()) {
            case 0:
                status = "待发货";
                holder.btnConfirmReceipt.setVisibility(View.GONE);
                break;
            case 1:
                status = "已发货";
                holder.btnConfirmReceipt.setVisibility(View.VISIBLE);
                break;
            case 2:
                status = "已收货";
                holder.btnConfirmReceipt.setVisibility(View.GONE);
                break;
            default:
                status = "未知状态";
                holder.btnConfirmReceipt.setVisibility(View.GONE);
                break;
        }
        holder.tvStatus.setText("状态: " + status);
        
        // 确认收货按钮点击事件
        holder.btnConfirmReceipt.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onConfirmReceipt(position, order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Order> getList() {
        return list;
    }

    public void setList(List<Order> orders) {
        this.list.clear();
        if (orders != null) {
            this.list.addAll(orders);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onConfirmReceipt(int position, Order order);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookCover;
        TextView tvBookName, tvQuantity, tvOrderTime, tvStatus;
        Button btnConfirmReceipt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
            tvBookName = itemView.findViewById(R.id.tv_book_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvOrderTime = itemView.findViewById(R.id.tv_order_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnConfirmReceipt = itemView.findViewById(R.id.btn_confirm_receipt);
        }
    }
} 