package com.example.application.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.application.databinding.ActivityProductDetailBinding;
import com.example.application.model.Product;

import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 任务: 详细信息屏幕 - 级别 2
        // 描述: 从启动此Activity的Intent中获取传递过来的Product对象。
        Product product = (Product) getIntent().getSerializableExtra("PRODUCT_EXTRA");

        if (product != null) {
            // 任务: 详细信息屏幕 - 级别 2
            // 描述: 将Product对象的数据填充到UI控件中。
            binding.detailProductName.setText(product.getName());
            binding.detailProductPrice.setText(String.format(Locale.getDefault(), "¥ %.2f", product.getPrice()));
            binding.detailProductDescription.setText(product.getDescription());
            Glide.with(this)
                    .load(product.getImageUrl())
                    .into(binding.detailProductImage);
        }

        binding.myToolbar.setTitle("Product Details");

        // 2. 将 Toolbar 设置为 Action Bar
        setSupportActionBar(binding.myToolbar);
        // 添加返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
