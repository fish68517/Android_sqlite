package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.R;

public class TradingAccountListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_account_list);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.image_2);

        View account16Area = findViewById(R.id.click_area_account_16);
        account16Area.setOnClickListener(v -> {
            startDetailActivity("16");
        });

        View account65Area = findViewById(R.id.click_area_account_65);
        account65Area.setOnClickListener(v -> {
            startDetailActivity("65");
        });
    }

    private void startDetailActivity(String accountId) {
        Intent intent = new Intent(TradingAccountListActivity.this, StockDetailActivity.class);
        intent.putExtra("image_res_id", R.drawable.image_3);
        intent.putExtra("account_id", accountId);
        startActivity(intent);
    }
} 