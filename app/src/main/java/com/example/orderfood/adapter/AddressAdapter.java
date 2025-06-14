package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.UserAddress;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private Context context;
    private List<UserAddress> addresses;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UserAddress address);
        void onEditClick(UserAddress address);
        void onDeleteClick(UserAddress address);
        void onSetDefaultClick(UserAddress address);
    }

    public AddressAdapter(Context context, List<UserAddress> addresses) {
        this.context = context;
        this.addresses = addresses;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserAddress address = addresses.get(position);
        
        holder.nameText.setText(address.getName());
        holder.phoneText.setText(address.getPhone());
        holder.addressText.setText(address.getAddress());
        
        // 设置默认标记
        holder.defaultTag.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);
        
        // 设置点击事件
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(address);
            }
        });
        
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(address);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(address);
            }
        });
        
        holder.setDefaultButton.setVisibility(address.isDefault() ? View.GONE : View.VISIBLE);
        holder.setDefaultButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSetDefaultClick(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView nameText;
        TextView phoneText;
        TextView addressText;
        TextView defaultTag;
        ImageButton editButton;
        ImageButton deleteButton;
        MaterialButton setDefaultButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            nameText = itemView.findViewById(R.id.nameText);
            phoneText = itemView.findViewById(R.id.phoneText);
            addressText = itemView.findViewById(R.id.addressText);
            defaultTag = itemView.findViewById(R.id.defaultTag);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            setDefaultButton = itemView.findViewById(R.id.setDefaultButton);
        }
    }
} 