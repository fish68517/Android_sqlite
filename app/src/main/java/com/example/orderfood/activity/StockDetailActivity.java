package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.R;


public class StockDetailActivity extends AppCompatActivity {

    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        // Retrieve and store the account ID from the intent
        accountId = getIntent().getStringExtra("account_id");

        ImageView imageView = findViewById(R.id.stock_detail_image);
        int imageResId = getIntent().getIntExtra("image_res_id", 0);

        if (imageResId != 0) {
            imageView.setImageResource(imageResId);
        }

        View fingerprintIconArea = findViewById(R.id.click_area_fingerprint_icon);
        fingerprintIconArea.setOnClickListener(v -> {
            Intent intent = new Intent(StockDetailActivity.this, TradeActivity.class);
            // Pass the stored account ID to the next activity
            intent.putExtra("account_id", accountId);
            startActivity(intent);
        });

        View cancelArea = findViewById(R.id.click_area_cancel);
        cancelArea.setOnClickListener(v -> {
            finish(); // Close the current activity
        });
    }
} 