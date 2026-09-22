package com.example.campusguide.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusguide.PlaceDetailActivity;
import com.example.campusguide.R;
import com.example.campusguide.model.Place;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private final Context context;
    private final List<Place> banners;

    public BannerAdapter(Context context, List<Place> banners) {
        this.context = context;
        this.banners = banners;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BannerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Place place = banners.get(position);
        holder.image.setImageResource(place.getImageResId());
        holder.title.setText(place.getName());
        holder.subtitle.setText(place.getShortDescription());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaceDetailActivity.class);
            intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, place.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return banners.size(); }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title;
        final TextView subtitle;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_banner);
            title = itemView.findViewById(R.id.text_banner_title);
            subtitle = itemView.findViewById(R.id.text_banner_subtitle);
        }
    }
}
