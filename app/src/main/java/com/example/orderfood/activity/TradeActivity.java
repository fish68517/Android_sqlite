package com.example.orderfood.activity;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.R;


public class TradeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        ImageView tradeImageView = findViewById(R.id.trade_image);
        String accountId = getIntent().getStringExtra("account_id");

        int imageResId;
        if ("16".equals(accountId)) {
            imageResId = R.drawable.image_4_16;
        } else if ("65".equals(accountId)) {
            imageResId = R.drawable.image_4_65;
        } else {
            // Default or error image
            imageResId = R.drawable.image_4;
        }

        imageResId = R.drawable.image_4_16;
        tradeImageView.setImageResource(imageResId);
    }
} 