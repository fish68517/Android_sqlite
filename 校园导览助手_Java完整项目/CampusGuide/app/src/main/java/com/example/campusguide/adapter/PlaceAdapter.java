package com.example.campusguide.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusguide.FavoriteStore;
import com.example.campusguide.PlaceDetailActivity;
import com.example.campusguide.R;
import com.example.campusguide.model.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    public interface FavoriteChangedListener {
        void onFavoriteChanged();
    }

    private final Context context;
    private final List<Place> places = new ArrayList<>();
    private final FavoriteChangedListener listener;

    public PlaceAdapter(Context context, List<Place> initialPlaces, FavoriteChangedListener listener) {
        this.context = context;
        this.listener = listener;
        setPlaces(initialPlaces);
    }

    public void setPlaces(List<Place> newPlaces) {
        places.clear();
        places.addAll(newPlaces);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        holder.image.setImageResource(place.getImageResId());
        holder.name.setText(place.getName());
        holder.category.setText(place.getCategory());
        holder.description.setText(place.getShortDescription());
        renderFavorite(holder.favorite, FavoriteStore.isFavorite(context, place.getId()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaceDetailActivity.class);
            intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, place.getId());
            context.startActivity(intent);
        });

        holder.favorite.setOnClickListener(v -> {
            boolean added = FavoriteStore.toggle(context, place.getId());
            renderFavorite(holder.favorite, added);
            Toast.makeText(context, added ? "已加入收藏" : "已取消收藏", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onFavoriteChanged();
        });
    }

    private void renderFavorite(ImageButton button, boolean selected) {
        button.setColorFilter(selected
                ? ContextCompat.getColor(context, R.color.secondary_dark)
                : Color.parseColor("#A8B4B0"));
        button.setContentDescription(selected ? "取消收藏" : "加入收藏");
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView name;
        final TextView category;
        final TextView description;
        final ImageButton favorite;

        PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_place);
            name = itemView.findViewById(R.id.text_place_name);
            category = itemView.findViewById(R.id.text_place_category);
            description = itemView.findViewById(R.id.text_place_description);
            favorite = itemView.findViewById(R.id.button_favorite);
        }
    }
}
