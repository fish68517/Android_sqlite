package com.Health.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Health.R;
import com.Health.utils.ImageLoader;

import java.util.List;

/**
 * Banner适配器
 * 
 * @author Health
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private List<String> bannerList;

    public BannerAdapter(List<String> bannerList) {
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = bannerList.get(position % bannerList.size());
        ImageLoader.loadImage(imageUrl, holder.imageView);
    }

    @Override
    public int getItemCount() {
        return bannerList != null ? bannerList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_banner);
        }
    }
}

