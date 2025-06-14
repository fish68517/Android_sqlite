package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.model.MerchantBean;

import java.util.List;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder> {
    private List<MerchantBean> merchants;
    private Context context;
    private OnItemClickListener listener;

    public void setMerchants(List<MerchantBean> merchants) {
        this.merchants = merchants;

    }

    public interface OnItemClickListener {
        void onItemClick(MerchantBean merchant);
    }

    public MerchantAdapter(Context context, List<MerchantBean> merchants) {
        this.context = context;
        this.merchants = merchants;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MerchantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_merchant_card, parent, false);
        return new MerchantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantViewHolder holder, int position) {
        MerchantBean merchant = merchants.get(position);
        holder.bind(merchant);
    }

    @Override
    public int getItemCount() {
        return merchants != null ? merchants.size() : 0;
    }

    class MerchantViewHolder extends RecyclerView.ViewHolder {
        private ImageView merchantImage;
        private TextView merchantName;
        private RatingBar ratingBar;
        private TextView ratingText;
        private TextView salesText;
        private TextView minPriceText;
        private TextView discountInfo;
        private ImageView deliveryIcon;

        public MerchantViewHolder(@NonNull View itemView) {
            super(itemView);
            merchantImage = itemView.findViewById(R.id.merchantImage);
            merchantName = itemView.findViewById(R.id.merchantName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingText = itemView.findViewById(R.id.ratingText);
            salesText = itemView.findViewById(R.id.salesText);
            minPriceText = itemView.findViewById(R.id.minPriceText);
            discountInfo = itemView.findViewById(R.id.discountInfo);
            deliveryIcon = itemView.findViewById(R.id.deliveryIcon);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(merchants.get(getAdapterPosition()));
                }
            });
        }

        public void bind(MerchantBean merchant) {
            merchantName.setText(merchant.getName());
            if (merchant.getImageName() != null ) {
                // 设置商家图片
                int imageResId = context.getResources().getIdentifier(
                        merchant.getImageName(), "mipmap", context.getPackageName());
                merchantImage.setImageResource(imageResId);
            } else {
                merchantImage.setImageResource(R.mipmap.merchants_bawangchaji);
            }

            
            // 设置评分
            ratingBar.setRating((float) merchant.getRating());
            ratingText.setText(String.format("%.1f", merchant.getRating()));
            
            // 设置销量
            salesText.setText(String.format("月售%d", merchant.getSales()));
            
            // 设置起送价
            minPriceText.setText(String.format("起送¥%.2f", merchant.getMinPrice()));
            
            // 设置优惠信息
            if (merchant.getDiscountInfo() != null && !merchant.getDiscountInfo().isEmpty()) {
                discountInfo.setVisibility(View.VISIBLE);
                discountInfo.setText(merchant.getDiscountInfo());
            } else {
                discountInfo.setVisibility(View.GONE);
            }
            
            // 设置是否外送图标
            deliveryIcon.setVisibility(merchant.isDelivery() ? View.VISIBLE : View.GONE);
        }
    }
} 