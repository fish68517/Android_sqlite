package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.MerchantBean;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MerchantListAdapter extends RecyclerView.Adapter<MerchantListAdapter.ViewHolder> {
    private List<MerchantBean> merchants;
    private OnEditClickListener onEditClickListener;

    public interface OnEditClickListener {
        void onEditClick(MerchantBean merchant, String newPassword);
    }

    public MerchantListAdapter(List<MerchantBean> merchants) {
        this.merchants = merchants;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public void updateData(List<MerchantBean> newMerchants) {
        this.merchants = newMerchants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merchant_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MerchantBean merchant = merchants.get(position);
        holder.bind(merchant);
    }

    @Override
    public int getItemCount() {
        return merchants.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMerchantName;
        private TextView tvLocation;
        private TextView tvBusinessHours;
        private TextInputEditText etNewPassword;
        private Button btnEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMerchantName = itemView.findViewById(R.id.tvMerchantName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvBusinessHours = itemView.findViewById(R.id.tvBusinessHours);
            etNewPassword = itemView.findViewById(R.id.etNewPassword);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }

        void bind(MerchantBean merchant) {
            tvMerchantName.setText(merchant.getName());
            tvLocation.setText("位置: " + merchant.getWindowLocation());
            tvBusinessHours.setText("营业时间: " + merchant.getBusinessHours());

            btnEdit.setOnClickListener(v -> {
                String newPassword = etNewPassword.getText().toString().trim();
                if (newPassword.isEmpty()) {
                    etNewPassword.setError("请输入新密码");
                    return;
                }
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(merchant, newPassword);
                }
                etNewPassword.setText("");
            });
        }
    }
} 