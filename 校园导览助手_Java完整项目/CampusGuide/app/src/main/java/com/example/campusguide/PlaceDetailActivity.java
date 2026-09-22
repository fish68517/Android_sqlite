package com.example.campusguide;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusguide.model.Place;
import com.google.android.material.button.MaterialButton;

public class PlaceDetailActivity extends AppCompatActivity {
    public static final String EXTRA_PLACE_ID = "place_id";
    private Place place;
    private MaterialButton favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        place = CampusData.findPlace(getIntent().getIntExtra(EXTRA_PLACE_ID, 1));
        findViewById(R.id.button_back).setOnClickListener(v -> finish());
        findViewById(R.id.image_detail).setBackgroundResource(R.color.primary_light);
        ((android.widget.ImageView) findViewById(R.id.image_detail)).setImageResource(place.getImageResId());
        ((android.widget.TextView) findViewById(R.id.text_detail_category)).setText(place.getCategory());
        ((android.widget.TextView) findViewById(R.id.text_detail_name)).setText(place.getName());
        ((android.widget.TextView) findViewById(R.id.text_detail_hours)).setText("开放时间：" + place.getHours());
        ((android.widget.TextView) findViewById(R.id.text_detail_distance)).setText(place.getDistance());
        ((android.widget.TextView) findViewById(R.id.text_detail_description)).setText(place.getDescription());

        favoriteButton = findViewById(R.id.button_detail_favorite);
        updateFavoriteButton();
        favoriteButton.setOnClickListener(v -> {
            boolean added = FavoriteStore.toggle(this, place.getId());
            updateFavoriteButton();
            Toast.makeText(this, added ? "已加入收藏" : "已取消收藏", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateFavoriteButton() {
        boolean favorite = FavoriteStore.isFavorite(this, place.getId());
        favoriteButton.setText(favorite ? "取消收藏" : "加入收藏");
    }
}
