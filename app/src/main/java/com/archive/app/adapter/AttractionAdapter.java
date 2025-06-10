package com.archive.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.activity.AttractionDetailActivity;
import com.archive.app.model.Attraction;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.List;
import java.util.Locale;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    private final Context context;
    private final List<Attraction> attractionList;

    public AttractionAdapter(Context context, List<Attraction> attractionList) {
        this.context = context;
        this.attractionList = attractionList;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        holder.nameTextView.setText(attraction.getName());
        holder.locationTextView.setText(attraction.getLocation());
        holder.priceTextView.setText(String.format(Locale.getDefault(), "￥%.2f", attraction.getPrice()));

        // 从数据库获取图片名称字符串
        String imageName = attraction.getImageUrl();
        int imageResId = 0;
        if (imageName != null && !imageName.isEmpty()) {
            // 移除文件扩展名
            String drawableName = imageName.substring(0, imageName.lastIndexOf('.'));
            // 动态获取资源ID
            imageResId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
        }

        Glide.with(context)
                .load(imageResId) // 加载获取到的资源ID
                .error(R.drawable.attraction_image4) // 如果ID为0或加载失败，显示默认图片
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AttractionDetailActivity.class);
            intent.putExtra(AttractionDetailActivity.EXTRA_ATTRACTION_ID, attraction.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    static class AttractionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView locationTextView;
        TextView priceTextView;

        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.attraction_image);
            nameTextView = itemView.findViewById(R.id.attraction_name);
            locationTextView = itemView.findViewById(R.id.attraction_location);
            priceTextView = itemView.findViewById(R.id.attraction_price);
        }
    }
} 