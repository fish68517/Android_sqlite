package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.R;
import com.example.orderfood.model.MerchantBean;

public class MerchantDetailActivity extends AppCompatActivity {

    private ImageView imageViewMerchant;
    private TextView textViewName, textViewWindowLocation, textViewBusinessHours, textViewContent, textViewBrowseCount;
    private TextView dishList;
    private int merchantId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_detail);

        imageViewMerchant = findViewById(R.id.imageViewMerchant);
        textViewName = findViewById(R.id.textViewName);
        textViewWindowLocation = findViewById(R.id.textViewWindowLocation);
        textViewBusinessHours = findViewById(R.id.textViewBusinessHours);
        textViewContent = findViewById(R.id.textViewContent);
        dishList = findViewById(R.id.dish_content);
        merchantId = getIntent().getIntExtra("merchantId", -1);
        dishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到商家的菜品列表
                Bundle bundle = new Bundle();
                bundle.putInt("merchantId", merchantId);
                Intent intent = new Intent(MerchantDetailActivity.this, DishListActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        textViewBrowseCount = findViewById(R.id.textViewBrowseCount);
        fetchMerchantDetails(merchantId);
    }




    private void fetchMerchantDetails(int merchantId) {
        DBMysqlHelper.getInstance(this).getMerchantInfo(merchantId, new DBMysqlHelper.DatabaseCallback<MerchantBean>() {
            @Override
            public void onSuccess(MerchantBean result) {
                MerchantBean merchant = result;
                textViewName.setText(merchant.getName());
                textViewWindowLocation.setText(merchant.getWindowLocation());
                textViewBusinessHours.setText("活动：" + merchant.getDiscountInfo());
                textViewContent.setText("描述：" + merchant.getContent());
                // 设置商家图片
                int imageResId = getResources().getIdentifier(
                        merchant.getImageName(), "mipmap",getPackageName());
                imageViewMerchant.setImageResource(imageResId);
            }

            @Override
            public void onError(Exception e) {

                System.out.println("error: " + e.getMessage());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
